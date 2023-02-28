# ci-management

[Servana](https://servanamanaged.com) is responsible for the management and maintenance for the Jenkins controller and the Jenkins agents. We will continue to support a open and accesible Jenkins service for the DentOS Project.

This repo contains the Jenkins Job Builder scripts for the Dent project.

### Browse Jenkins Controller

-   Jenkins Production: https://dent.jenkins.app

### Download Artifacts

-   Artifacts: https://repos.refinery.dev/service/rest/repository/browse/dent/

## Have any issues?

If you require support please contact Servana support. Get support by [submitting a ticket](https://support.servanamanaged.com/support/tickets/new?ticket_form=request_support). We will get back to you as soon as possible.

### SLA

| **Feature**                    | **Level 1**     |
| ------------------------------ | --------------- |
| Support Hours                  | Business Hours  |
| Response                       | 4hr Response    |
| Resolution                     | 12hr Resolution |
| Uptime                         | 99%             |
| Contact Method                 | Email, Website  |
| Multi-Region Disaster Recovery | 8hrs            |

## Access/Signup requests to Jenkins

-   The Jenkins controller is available with read-only access. If you require greater levels of access please contact Servana Support. No access requests are guaranteed and we will seek approvals where necessary from the projects Technical Steering Committee.
-   The Jenkins controller is integrated with github and signing in will grant read-only access.

## Commiting

All commits must be signed with a GPG key.

## Pre-commit

1. Create a GPG key set it up as part of your Github account.
2. Turn on [commit verification](https://docs.github.com/en/authentication/managing-commit-signature-verification/about-commit-signature-verification)
3. This repository follows the rules set by [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/). Read the specification before making any commits it is your responsibility to rebase and correct any failed precommit messages.
4. Finally this repository requires the author of a commit to sign their work. To sign a commit `git commit -S -m 'feat:|chore: the message etc'`

The following checks will run:
- trailing-whitespace
- prettier
- gitlint
- yamllint
- shellcheck