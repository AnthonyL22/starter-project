import json
import requests

API_KEY = 'gwZM1vNTdxZuIN2YUhp9'
ROOT_URL = 'http://localhost/api/v1/'
ENDPOINT = 'incidents'


def create_incident(incident_name, incident_message, incident_status, incident_visible, component_id, component_status):
    url = ROOT_URL + ENDPOINT
    payload = {"name": incident_name, "message": incident_message, "status": incident_status,
               "visible": incident_visible,
               "component_id": component_id, "component_status": component_status}
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    requests.post(url, data=json.dumps(payload), headers=headers)


if __name__ == "__main__":
    create_incident()
