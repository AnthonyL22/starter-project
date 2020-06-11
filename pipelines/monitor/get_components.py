import json
import sys
import requests

API_KEY = 'gwZM1vNTdxZuIN2YUhp9'
ROOT_URL = 'http://localhost/api/v1/'
ENDPOINT = 'components'


def get_all_components():
    url = ROOT_URL + ENDPOINT

    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}

    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    return json_data


def get_component_by_id(component_id):
    url = ROOT_URL + ENDPOINT + "/" + str(component_id)

    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}

    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    return json_data


def get_component_by_field(component_id, field):
    url = ROOT_URL + ENDPOINT + "/" + str(component_id)

    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}

    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    field_value = json_data['data'][field]
    return field_value


if __name__ == "__main__":
    rootURL = sys.argv[1]
    get_all_components()
