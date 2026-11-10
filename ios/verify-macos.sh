#!/bin/bash
set -euo pipefail

if [[ "$(uname -s)" != "Darwin" ]]; then
  echo "This check requires macOS and the full Xcode application." >&2
  exit 1
fi

cd "$(dirname "$0")"
xcodebuild -version
xcrun --sdk iphonesimulator --show-sdk-path
plutil -lint Beloz.xcodeproj/project.pbxproj
plutil -lint Beloz/Resources/PrivacyInfo.xcprivacy

test_dir="$(mktemp -d "${TMPDIR:-/tmp}/beloz-contract-tests.XXXXXX")"
trap 'rm -rf "$test_dir"' EXIT
swiftc Beloz/Sources/Models.swift Beloz/Sources/Discovery.swift Tests/ContractTests.swift -o "$test_dir/contracts"
"$test_dir/contracts"

# A simulator build does not need a developer signing identity or a database.
xcodebuild -project Beloz.xcodeproj -scheme Beloz -configuration Debug \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath "${TMPDIR:-/tmp}/BelozVerification" \
  CODE_SIGNING_ALLOWED=NO build

echo "Simulator build passed. Account, network and device tests remain manual."
