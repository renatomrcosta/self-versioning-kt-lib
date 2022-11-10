.PHONY: test
test:
	./gradlew check

.PHONY: semantic-release
semantic-release:
	npx semantic-release

.PHONY: publish
publish:
	./gradlew -Pversion=$(TAG) publish
