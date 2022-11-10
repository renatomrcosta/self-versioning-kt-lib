# Self Versioning Kotlin Lib

Simple experiment to build a template for Kotlin libraries that self manage their SemVer version, publishing in GitHub

### Setup

Ensure your repo has a secret named `PACKAGE_RELEASE_TOKEN` set with a token that has permissions to release packages (`write:packages`). This can be changed in the Github actions setup in the repo, as one wishes.

### How it works

This requires one discipline: Commits have to follow the [Angular Commit Message format](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#-commit-message-format).

Then our CI uses the [semantic-release](https://github.com/semantic-release/semantic-release) npm package. It uses some basic configuration described in `package.json` and `.releaserc.json` files. This plugin can take your commits from HEAD until the previous TAG, and use their commit messages to determine which version (MAJOR, MINOR or PATCH) should be incremented when generating a new version. 

Then the actions: The two workflows create are independent of one another:

1. The `semantic_release.yml` workflow triggers whenever main is pushed to. It will run tests and then run the semantic release plugin. The plugin is solely responsible for generating a new git tag and github release in case the version should be bumped.

2. The `publish.yml` workflow triggers whenever a release is created (so it can also react to manual releases). It will take the latest git HEAD, stripe away the characters we don't want in the version number (namely, `/refs/HEAD/v.1.2.30` becomes `1.2.30` for example), and call the gradle publish task passing this version as a parameter. This works because we can specify a substitution to the `version` key in the gradle.properties file via the cmd line.
