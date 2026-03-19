# takeover

challeng says the solution has something to do with subdomain enumeration

probably important hint is that they are "rebuilding their support", maybe support.futurevera.thm?

---

`ffuf -w .local/share/SecLists/Discovery/DNS/subdomains-top1million-110000.txt -H 'Host: FUZZ.futurevera.thm' -u http://futurevera.thm -fs 0`

we get:

- payroll.futurevera.thm
- portal.futurevera.thm

getting either of these pages yields the response:

<h1>
    payroll.futurevera.thm is only available via internal VPN
</h1>

---

i tried to think of some way to get around this, unfortunately im a noob, the only thing i can think was manipulating origin or referer headers but that obviously won't work and the challenge is about subdomain takeover anyway..

---

one thing that is interesting is the use of https, it is common for subdomains to leak in the tls cert

- the cert for the main page doesn't give us anything

however if we view the tls cert for support.futurevera.thm as mentioned earlier, we see a subject alt name of secrethelpdesk934752.support.futurevera.thm

requesting this will give us a redirect to an abandoned s3 bucket, where the subdomain is the flag.

an attacker could take over that name and direct users to their site !!

