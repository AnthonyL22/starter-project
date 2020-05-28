import base64

import requests

KEY = "6HB3bS6pbUprwiR"
ENCODED_USERNAME = "maejoc7Co9I="
ENCODED_PASSWORD = "hrGun8TCrqWTiKGT"

static_response = None
ENDPOINT = None


class HealthCheck:
    current_response = None

    def __init__(self, endpoint):
        self.username = self.dec(KEY, ENCODED_USERNAME)
        self.password = self.dec(KEY, ENCODED_PASSWORD)
        self.session = requests.Session()
        self.current_response = self.page(endpoint)

    def page(self, endpoint):
        _page = Page(self, endpoint)
        response = _page.application_alive()
        print("Application Alive? {0!s}".format(True if response.status_code == 200 else False))
        return response

    def _get(self, endpoint):
        print('Performing Health Check for: ' + endpoint)
        response = self.session.get(endpoint, verify=False)
        if response.status_code == 401:
            response.request.prepare_auth(auth=(self.username, self.password))
            response = self.session.send(response.request)
        return response

    @staticmethod
    def enc(key, clear):
        enc = []
        for i in range(len(clear)):
            key_c = key[i % len(key)]
            enc_c = chr((ord(clear[i]) + ord(key_c)) % 256)
            enc.append(enc_c)
        return base64.urlsafe_b64encode("".join(enc))

    @staticmethod
    def dec(key, enc):
        dec = []
        enc = base64.urlsafe_b64decode(enc)
        for i in range(len(enc)):
            key_c = key[i % len(key)]
            dec_c = chr((256 + ord(enc[i]) - ord(key_c)) % 256)
            dec.append(dec_c)
        return "".join(dec)



class Page:

    def __init__(self, application_monitored, endpoint):
        self.application = application_monitored
        self.endpoint = endpoint
        self._latest_response = None

    def application_alive(self):
        return self.application._get(endpoint=self.endpoint)


if __name__ == "__main__":
    HealthCheck(ENDPOINT)
