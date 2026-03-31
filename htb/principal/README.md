# principal

initial port scan:

- 22: ssh
- 8080: http

X-Powered-By header: `pac4j-jwt/6.0.3` [CVE-2026-29000](https://nvd.nist.gov/vuln/detail/CVE-2026-29000)

main js file: `/static/js/app.js`

```javascript
/**
 * Principal Internal Platform - Client Application
 * Version: 1.2.0
 *
 * Authentication flow:
 * 1. User submits credentials to /api/auth/login
 * 2. Server returns encrypted JWT (JWE) token
 * 3. Token is stored and sent as Bearer token for subsequent requests
 *
 * Token handling:
 * - Tokens are JWE-encrypted using RSA-OAEP-256 + A128GCM
 * - Public key available at /api/auth/jwks for token verification
 * - Inner JWT is signed with RS256
 *
 * JWT claims schema:
 *   sub   - username
 *   role  - one of: ROLE_ADMIN, ROLE_MANAGER, ROLE_USER
 *   iss   - "principal-platform"
 *   iat   - issued at (epoch)
 *   exp   - expiration (epoch)
 */
```

after [editing the poc](src/main/java/Poc.java) in the [writeup for the cve](https://www.codeant.ai/security-research/pac4j-jwt-authentication-bypass-public-key) to include the claims we need according to the schema and pull the jwks from the pubkey endpoint, place the generated token in session storage at auth_token

```javascript
function renderNavigation(role) {
    const navItems = [
        { label: 'Dashboard', endpoint: DASHBOARD_ENDPOINT, roles: [ROLES.ADMIN, ROLES.MANAGER, ROLES.USER] },
        { label: 'Users', endpoint: USERS_ENDPOINT, roles: [ROLES.ADMIN] },
        { label: 'Settings', endpoint: SETTINGS_ENDPOINT, roles: [ROLES.ADMIN] },
    ];

    return navItems.filter(item => item.roles.includes(role));
}
```

```javascript
static setToken(token) {
    sessionStorage.setItem('auth_token', token);
}
```

inside the dashboard under settings we can find the encryptionKey in plaintext

now we need to find which user can use this password to ssh into the box. to do this pull the response from /api/users and parse out all the users with jq so we can spray it with hydra

`jq -r '.users[].username' users.json > users.list`

`hydra -L users.list -p <THE_PASSWORD> ssh://target`

the user svc-deploy uses this password, this account is described as a service account for automated deployments via ssh CA in the dashboard

ssh in -> first flag

---

other things to note in the dashboard are a note saying that the SSH CA auth is configured for automation, and to see /opt/principal/ssh/ for the CA config

svc-deploy is in the "deployers" group which has read access to the mentioned dir

there is a readme in there that states the CA (also in that dir) is trusted for cert-based auth via SSH. we also find a custom sshd config in `/etc/ssh/sshd_config.d/60-principal.conf`, this configuration explicitly trusts the CA we have access to BUT doesn't check the principal, meaning we can forge a cert for any user we want

there is also mention of a deploy.sh script somewhere that could be used to generate certs for service accounts, we don't seem to have access but the process is very simple:

`ssh-keygen -s ca -I pwned -n root id_root_principal.pub`

![pwned](/images/20260331_16h48m55s_grim.png)
