import json
import requests
import challenge_api as challenge

API_KEY = 'gwZM1vNTdxZuIN2YUhp9'
ROOT_URL = 'http://localhost/api/v1/'
ENDPOINT = 'incidents?per_page=30000'
DELETION_ENDPOINT = 'incidents'


######################################################################
#   Find all incidents for all Components
######################################################################
def get_all_incidents():
    url = ROOT_URL + ENDPOINT
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    return json_data


######################################################################
#   Find all incidents for a given Component ID
######################################################################
def get_incident_list_by_component_id(component_id):
    url = ROOT_URL + ENDPOINT
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    page_count = json_data['meta']['pagination']['total']
    if page_count > 0:
        incidents_found_for_component_id = list()
        all_incidents_list = json_data['data']
        for incident_index, located_component_id in enumerate(d['component_id'] for d in all_incidents_list):
            if located_component_id == component_id:
                incidents_found_for_component_id.append(all_incidents_list[incident_index])
        return incidents_found_for_component_id


######################################################################
#   Find all incidents for a given Component ID that has a status > the
#   given Status ID
######################################################################
def get_incident_list_by_component_id_and_greater_than_status_id(component_id, status_id):
    url = ROOT_URL + ENDPOINT
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    page_count = json_data['meta']['pagination']['total']
    if page_count > 0:
        incident_list_found_for_component_id = list()
        all_incidents_list = json_data['data']
        for incident_index, located_component_id in enumerate(d['component_id'] for d in all_incidents_list):
            if located_component_id == component_id:
                located_component = all_incidents_list[incident_index]
                located_component_status = located_component['status']
                if located_component_status > status_id:
                    incident_list_found_for_component_id.append(all_incidents_list[incident_index])

        return sorted(incident_list_found_for_component_id, key=lambda k: k['id'], reverse=True)


######################################################################
#   Check if the oldest incident in the sorted list is worse than
#   the previous incident's status ID
######################################################################
def is_oldest_incident_worse_than_previous_reported(sorted_list):
    try:
        if sorted_list[0]['status'] < sorted_list[1]['status'] and sorted_list[0]['status'] != 0 or \
                (sorted_list[0]['status'] == challenge.INVESTIGATING_INCIDENT_STATUS and
                 sorted_list[1]['status'] == challenge.INVESTIGATING_INCIDENT_STATUS):
            return True
        else:
            return False
    except IndexError as err:
        return False


######################################################################
#   Check if the last 2 incidents where failures.  If so, return true
######################################################################
def has_failed_last_two_times_with_identical_failure(sorted_list):
    try:
        if sorted_list[0]['status'] == sorted_list[1]['status'] and \
                (sorted_list[0]['status'] != challenge.SCHEDULED_INCIDENT_STATUS and
                 sorted_list[1]['status'] != challenge.SCHEDULED_INCIDENT_STATUS):
            return True
        else:
            return False
    except:
        return False


######################################################################
#   Find an incident by its Incident ID
######################################################################
def get_incident_by_id(incident_id):
    url = ROOT_URL + ENDPOINT + "/" + incident_id
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    response = requests.get(url, headers=headers)
    json_data = json.loads(response.text)
    return json_data


######################################################################
#   Delete an incident by its Incident ID
######################################################################
def delete_incident_by_incident_id(incident_id):
    url = ROOT_URL + DELETION_ENDPOINT + "/" + str(incident_id)
    headers = {'X-Cachet-Token': API_KEY, 'Content-Type': 'application/json'}
    requests.delete(url, headers=headers)


######################################################################
#   Delete all incidents in the given incident list
######################################################################
def delete_incident_list(incident_list):
    if incident_list:
        for i, incident_id in enumerate(d['id'] for d in incident_list):
            print "Deleting Incident ID={0!s}".format(str(incident_id))
            delete_incident_by_incident_id(incident_id)


if __name__ == "__main__":
    get_all_incidents()
