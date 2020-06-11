import json
import sys

import requests

root_component_id = ""
root_component_status = ""

API_KEY = 'gwZM1vNTdxZuIN2YUhp9'
ROOT_URL = 'http://localhost/api/v1/'
ENDPOINT = 'components'


def update_component():
    url = ROOT_URL + ENDPOINT + "/" + str(root_component_id)
    payload = {"status": root_component_status}
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    requests.put(url, data=json.dumps(payload), headers=headers)


if __name__ == "__main__":
    root_component_id = sys.argv[1]
    root_component_status = sys.argv[2]
    update_component()
