import asyncio
import json
import os
import sys
import unittest
from pathlib import Path
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

# Prevent tests from loading the developer's .env or contacting PostgreSQL.
with patch.dict(os.environ, {
    "DATABASE_URL": "postgresql+asyncpg://test:test@127.0.0.1/beloz_test",
    "AUTO_CREATE_TABLES": "false",
    "CORS_ORIGINS": "*",
}):
    with patch("dotenv.load_dotenv", return_value=False):
        from app.database import get_session
        from app.main import app, hash_password, register, verify_password
        from app.models import User
        from fastapi import HTTPException


async def request(method, path, body=None, authorization=None, extra_headers=None, return_headers=False):
    headers = [(b"host", b"testserver"), (b"content-type", b"application/json")]
    if authorization:
        headers.append((b"authorization", authorization.encode("ascii")))
    headers.extend(extra_headers or [])
    scope = {
        "type": "http",
        "asgi": {"version": "3.0", "spec_version": "2.3"},
        "http_version": "1.1",
        "method": method,
        "scheme": "http",
        "path": path,
        "raw_path": path.encode("ascii"),
        "query_string": b"user_id=1",
        "headers": headers,
        "client": ("127.0.0.1", 12345),
        "server": ("testserver", 80),
    }
    messages = []
    sent_request = False
    response_finished = asyncio.Event()

    async def receive():
        nonlocal sent_request
        if not sent_request:
            sent_request = True
            return {"type": "http.request", "body": json.dumps(body or {}).encode(), "more_body": False}
        await response_finished.wait()
        return {"type": "http.disconnect"}

    async def send(message):
        messages.append(message)
        if message["type"] == "http.response.body" and not message.get("more_body", False):
            response_finished.set()

    await asyncio.wait_for(app(scope, receive, send), timeout=5)
    response = next(message for message in messages if message["type"] == "http.response.start")
    if return_headers:
        return response["status"], dict(response["headers"])
    return response["status"]


class SecurityRoutesTest(unittest.IsolatedAsyncioTestCase):
    async def test_sensitive_routes_fail_closed_without_a_trusted_session(self):
        routes = [
            ("GET", "/api/pedidos"),
            ("POST", "/api/pedidos/crear"),
            ("GET", "/api/pedidos/1/detalles"),
            ("POST", "/api/pedidos/1/detalles"),
            ("GET", "/api/pagos/1"),
            ("PUT", "/api/pagos/1"),
        ]
        for method, path in routes:
            for authorization in (None, "Bearer unverified-token"):
                with self.subTest(method=method, path=path, authorization=bool(authorization)):
                    self.assertEqual(await request(method, path, authorization=authorization), 503)

    async def test_health_check_remains_available(self):
        self.assertEqual(await request("GET", "/salud"), 200)

    async def test_lifespan_starts_without_database_when_auto_create_is_disabled(self):
        with patch.dict(os.environ, {"AUTO_CREATE_TABLES": "false"}):
            async with app.router.lifespan_context(app):
                self.assertEqual(await request("GET", "/salud"), 200)

    async def test_wildcard_cors_does_not_allow_credentials(self):
        status, headers = await request(
            "OPTIONS",
            "/api/restaurantes",
            extra_headers=[
                (b"origin", b"https://example.invalid"),
                (b"access-control-request-method", b"GET"),
            ],
            return_headers=True,
        )
        self.assertEqual(status, 200)
        self.assertEqual(headers.get(b"access-control-allow-origin"), b"*")
        self.assertNotIn(b"access-control-allow-credentials", headers)

    async def test_catalog_and_chat_remain_available(self):
        class EmptyResult:
            def scalars(self):
                return self

            def all(self):
                return []

        class EmptySession:
            async def execute(self, _statement):
                return EmptyResult()

        async def get_test_session():
            yield EmptySession()

        app.dependency_overrides[get_session] = get_test_session
        try:
            self.assertEqual(await request("GET", "/api/restaurantes"), 200)
            self.assertEqual(await request("POST", "/api/chat", {"message": "hola"}), 200)
        finally:
            app.dependency_overrides.pop(get_session, None)

    async def test_password_hash_round_trip(self):
        hashed = hash_password("test-password")
        self.assertTrue(verify_password("test-password", hashed))
        self.assertFalse(verify_password("wrong-password", hashed))

    async def test_registration_does_not_return_database_errors(self):
        class FailingSession:
            def add(self, _user):
                pass

            async def commit(self):
                raise RuntimeError("private database exception marker")

            async def rollback(self):
                pass

        user = User(email="test@example.invalid", password_hash="placeholder")
        with patch("app.main.hash_password", return_value="test-hash"):
            with self.assertRaises(HTTPException) as error:
                await register(user, FailingSession())
        self.assertEqual(error.exception.status_code, 400)
        self.assertNotIn("private database exception marker", error.exception.detail)


if __name__ == "__main__":
    unittest.main()
