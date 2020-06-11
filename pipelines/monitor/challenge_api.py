import json
import logging
import os
import sys
from datetime import datetime

import requests
import urllib3

import get_components as components
import get_incidents as get_incidents
import notification as notify
import set_incidents as set_incidents

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
urllib3.disable_warnings(urllib3.exceptions.InsecurePlatformWarning)

root_url = ''
headers = ''
is_post = False
body = ''
root_component_id = -1

access_token = ''
vds_id = ''

EMAIL_LOG_FILENAME = 'email_sent.log'
DEFAULT_TIMEOUT = 60
INCIDENT_NOT_VISIBLE = 2
INCIDENT_VISIBLE = 1

EXPERIENCING_ISSUES_MESSAGE = "Experiencing {0!s} status errors :: {1!s}"
CONTINUALLY_EXPERIENCING_ISSUES_MESSAGE = "Continually experiencing {0!s} status errors :: {1!s}"

MAX_RESPONSE_TIME = 10

# Incident Status'
SCHEDULED_INCIDENT_STATUS = 0
INVESTIGATING_INCIDENT_STATUS = 1
IDENTIFIED_INCIDENT_STATUS = 2
WATCHING_INCIDENT_STATUS = 3
FIXED_INCIDENT_STATUS = 4

# Component Status'
OPERATIONAL_COMPONENT_STATUS = 1
PERFORMANCE_ISSUES_COMPONENT_STATUS = 2
PARTIAL_OUTAGE_COMPONENT_STATUS = 3
MAJOR_OUTAGE_COMPONENT_STATUS = 4

# Common Error Information
AVAILABLE_LABEL = "Application Available"
UNAVAILABLE_LABEL = "Application Unavailable"
DEGRADED_PERFORMANCE_LABEL = "Degraded Performance"
OUTAGE_LABEL = "Application Outage"
OUTAGE_ESCALATION_LABEL = "Application Outage Escalation"

"""
PWC monitored endpoint UPTime Interrogation.

    The challenge module determines if the invoked URI from monitor.py is siteminder protected or not
    Once authorization has occurred the particular component is modified in the status page with the
    state of the invoked uri. components are articulated by uptime component_status and incident_status.

    Args:
        uri: A mix of RESTful endpoints and classic URLs.

        component_id: The unique identifier given to the URI in the cachethq configuration/settings

        email_contact: each uri has an associated escalation contact. Usually a list group or
        team email distribution list

        response: see requests package documentation.
        errRE: alias for the requests package exception class. Generic exception handler
        errCE: alias specific to the Connection Timeout exception handler. Captures 504 errors
        errHE: alias specific to the HTTPError exception handler. Captures 503 errors
        report_incident_and_component_status_from_http_response: update the status_page incident and status function

    Returns:
        The Requests python package retrieves the extended request/response attributes for each URI
        invoked. The returned object provides various HTTP result methods of interest such as those seen in the 
        response, errHE, errRE, errCE objects described in the section above.

    Raises:
        raise_for_status: Returns more granular exception handling values to errCE and errRE.
"""


def challenge_uri(self=None):
    try:
        access_token = ''
        vds_id = ''
        filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), EMAIL_LOG_FILENAME)
        logging.basicConfig(filename=filename, level=logging.INFO)
        if not headers:
            if is_post:
                response = requests.post(root_url, verify=False, timeout=DEFAULT_TIMEOUT)
            else:
                response = requests.get(root_url, verify=False, timeout=DEFAULT_TIMEOUT)
        else:
            if is_post:
                if body:
                    response = requests.post(root_url, headers=headers, json=body, verify=False,
                                             timeout=DEFAULT_TIMEOUT)
                    response_dict = json.loads(response.text)
                    if 'payload' in response_dict:
                        payload_dict = response_dict['payload']
                        if 'accessToken' in payload_dict:
                            access_token = response_dict['payload']['accessToken']
                            vds_id = response_dict['payload']['vdsId']
                else:
                    response = requests.post(root_url, headers=headers, verify=False, timeout=DEFAULT_TIMEOUT)
            else:
                response = requests.get(root_url, headers=headers, verify=False, timeout=DEFAULT_TIMEOUT)
        response.raise_for_status()
        if access_token != "":
            return report_incident_and_component_status_from_http_response(response), access_token, vds_id
        else:
            return report_incident_and_component_status_from_http_response(response)

    except requests.ConnectionError as errCE:

        ######################################################################
        # capture connection errors and return values needed for uptime logic.
        ######################################################################
        errCE.status_code = 504
        errCE.message = errCE.message.message
        return report_incident_and_component_status_from_http_response(errCE)

    except requests.HTTPError as errHE:

        ######################################################################
        #   Need to capture the exception.status_code and assign it to the response.status_code attribute
        #   Need to capture the exception.message and assign it to the response.message attribute
        ######################################################################
        errHE.status_code = errHE.response.status_code
        errHE.message = errHE.response.reason
        return report_incident_and_component_status_from_http_response(errHE)

    except requests.ReadTimeout as errRT:
        """
            Requests.ReadTimeout does not return an http status code directly.
            Sets status code returned in Browser. exceptions.py does not render the
            expected response.
        """
        errRT.status_code = 504
        errRT.message = errRT.message.message
        return report_incident_and_component_status_from_http_response(errRT)

    except requests.RequestException as errRE:

        return report_incident_and_component_status_from_http_response(errRE)


######################################################################
#   Find the value of a dynamically generated exception attribute
######################################################################
def get_first_attr(obj, *attributes):
    return next((val for val in (getattr(obj, attr, None) for attr in attributes)
                 if val is not None), None)


######################################################################
#   Based on a list of errors provided this will report an Incident to
#   the correct Cachet component if error count is > 0
######################################################################
def report_incident_and_component_status_from_error_list(errors_discovered_list):
    error_count = errors_discovered_list.__len__()
    print('Found {0!s} Errors'.format(error_count))

    # incident_message = "Recovered and Operational"
    # set_incidents.create_incident(AVAILABLE_LABEL, incident_message, FIXED_INCIDENT_STATUS,
    #                               INCIDENT_VISIBLE, root_component_id, OPERATIONAL_COMPONENT_STATUS)

    if error_count <= 0:
        print("Success: No Errors discovered in log file")
        send_operational_incident_update_list = \
            get_incidents.get_incident_list_by_component_id_and_greater_than_status_id(
                root_component_id, SCHEDULED_INCIDENT_STATUS)
        incident_update_is_needed = \
            get_incidents.is_oldest_incident_worse_than_previous_reported(send_operational_incident_update_list)
        if incident_update_is_needed:
            incident_message = "Systems Operational"
            set_incidents.create_incident(AVAILABLE_LABEL, incident_message, FIXED_INCIDENT_STATUS,
                                          INCIDENT_VISIBLE, root_component_id, OPERATIONAL_COMPONENT_STATUS)
    else:
        print("Fail: Errors discovered in log file")
        for error in errors_discovered_list:
            output_list = error.split()[:10]
            incident_message = "Currently experiencing '{0!s}' log error(s)".format(" ".join(output_list))
            set_incidents.create_incident(OUTAGE_LABEL, incident_message, INVESTIGATING_INCIDENT_STATUS,
                                          INCIDENT_VISIBLE, root_component_id, PARTIAL_OUTAGE_COMPONENT_STATUS)
            sys.exit(-1)


######################################################################
#   Based on a Qlikview log file timestamp issues this will report an Incident to
#   the correct Cachet component
######################################################################
def report_incident_and_component_status_from_timestamps(timestamp_in_violation, previous, current):
    if not timestamp_in_violation:
        send_operational_incident_update_list = \
            get_incidents.get_incident_list_by_component_id_and_greater_than_status_id(
                root_component_id, SCHEDULED_INCIDENT_STATUS)
        incident_update_is_needed = \
            get_incidents.is_oldest_incident_worse_than_previous_reported(send_operational_incident_update_list)
        if incident_update_is_needed:
            incident_message = "Systems Operational"
            set_incidents.create_incident(AVAILABLE_LABEL, incident_message, FIXED_INCIDENT_STATUS,
                                          INCIDENT_VISIBLE, root_component_id, OPERATIONAL_COMPONENT_STATUS)
    else:
        incident_message = "Qlikview log file mismatch " \
                           "Previous='{0!s}' vs. Latest='{1!s}' timestamps".format(previous, current)
        set_incidents.create_incident(OUTAGE_LABEL, incident_message, INVESTIGATING_INCIDENT_STATUS, INCIDENT_VISIBLE,
                                      root_component_id, PARTIAL_OUTAGE_COMPONENT_STATUS)


######################################################################
#   Based on the current_response this will report an Incident to
#   the correct Cachet component
######################################################################
def report_incident_and_component_status_from_http_response(response):

    if 200 <= response.status_code < 300:
        if response.elapsed.seconds > MAX_RESPONSE_TIME:

            incident_message = "Latent response time: {0!s}s. reported :: {1!s}". \
                format(response.elapsed.seconds, root_url)
            set_incidents.create_incident(DEGRADED_PERFORMANCE_LABEL, incident_message, WATCHING_INCIDENT_STATUS,
                                          INCIDENT_VISIBLE, root_component_id, PARTIAL_OUTAGE_COMPONENT_STATUS)
            return PERFORMANCE_ISSUES_COMPONENT_STATUS

        else:

            if components.get_component_by_field(root_component_id, "status") == OPERATIONAL_COMPONENT_STATUS:
                # Delete all existing Incidents if application's health reports OPERATIONAL
                # incidents_to_delete_list = get_incidents.get_incident_list_by_component_id(root_component_id)
                # get_incidents.delete_incident_list(incidents_to_delete_list)
                return OPERATIONAL_COMPONENT_STATUS
            else:
                incident_message = "Recovered and Operational"
                set_incidents.create_incident(AVAILABLE_LABEL, incident_message, FIXED_INCIDENT_STATUS,
                                              INCIDENT_VISIBLE, root_component_id, OPERATIONAL_COMPONENT_STATUS)

                # Delete all existing Incidents if application's health reports OPERATIONAL
                # incidents_to_delete_list = get_incidents.get_incident_list_by_component_id(root_component_id)
                # get_incidents.delete_incident_list(incidents_to_delete_list)
                return OPERATIONAL_COMPONENT_STATUS

    elif 300 <= response.status_code < 500:

        if components.get_component_by_field(root_component_id, "status") == MAJOR_OUTAGE_COMPONENT_STATUS:
            return MAJOR_OUTAGE_COMPONENT_STATUS
        elif components.get_component_by_field(root_component_id, "status") == PARTIAL_OUTAGE_COMPONENT_STATUS:

            # Escalate Component & Incident Status
            incident_message = CONTINUALLY_EXPERIENCING_ISSUES_MESSAGE.format(response.status_code, root_url)

            incident_update_list = get_incidents.get_incident_list_by_component_id_and_greater_than_status_id(
                root_component_id, SCHEDULED_INCIDENT_STATUS)
            failed_twice_consecutively = get_incidents.has_failed_last_two_times_with_identical_failure(
                incident_update_list)
            if not failed_twice_consecutively:
                set_incidents.create_incident(OUTAGE_ESCALATION_LABEL, incident_message, INVESTIGATING_INCIDENT_STATUS,
                                              INCIDENT_VISIBLE, root_component_id, MAJOR_OUTAGE_COMPONENT_STATUS)
            return MAJOR_OUTAGE_COMPONENT_STATUS

        else:

            incident_message = EXPERIENCING_ISSUES_MESSAGE.format(response.status_code, root_url)

            incident_update_list = get_incidents.get_incident_list_by_component_id_and_greater_than_status_id(
                root_component_id, SCHEDULED_INCIDENT_STATUS)
            failed_twice_consecutively = get_incidents.has_failed_last_two_times_with_identical_failure(
                incident_update_list)
            if not failed_twice_consecutively:
                set_incidents.create_incident(OUTAGE_LABEL, incident_message, WATCHING_INCIDENT_STATUS,
                                              INCIDENT_VISIBLE,
                                              root_component_id, PARTIAL_OUTAGE_COMPONENT_STATUS)
            return PARTIAL_OUTAGE_COMPONENT_STATUS

    elif response.status_code >= 500:

        if hasattr(response, 'message'):
            incident_message = "Experiencing {0!s} errors :: {1!s}\n\r{2!s}".format(
                response.status_code, root_url, response.message)
        else:
            incident_message = "Experiencing {0!s} errors :: {1!s}".format(
                response.status_code, root_url)

        set_incidents.create_incident(UNAVAILABLE_LABEL, incident_message, INVESTIGATING_INCIDENT_STATUS,
                                      INCIDENT_VISIBLE, root_component_id, PARTIAL_OUTAGE_COMPONENT_STATUS)

        incident_update_list = \
            get_incidents.get_incident_list_by_component_id_and_greater_than_status_id(
                root_component_id, SCHEDULED_INCIDENT_STATUS)
        failed_twice_consecutively = \
            get_incidents.has_failed_last_two_times_with_identical_failure(incident_update_list)

        ######################################################################
        #   Failure must happen 2 consecutive times for possible email notification.  Only 1 email notification/hr.
        ######################################################################
        if failed_twice_consecutively:
            if not email_sent_within_past_hour():
                notify.email_notify(incident_message, UNAVAILABLE_LABEL)
                logging.info("__{0!s}__{1!s}__{2!s}".format(str(datetime.now()), "Email Notification Sent", root_url))
            else:
                print('No email(s) sent for alert')
        return MAJOR_OUTAGE_COMPONENT_STATUS


######################################################################
#   Check the 'email_sent.log' for the last time an email was sent to the notification list within the past hour.
#   If an email was sent within the past 60 minutes then DO NOT send again.  If sent > 60 min ago then resend
#   email notification
######################################################################
def email_sent_within_past_hour():
    email_sent = False
    try:
        filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), EMAIL_LOG_FILENAME)
        with open(filename) as f:
            data = f.readlines()
        last_line = data[-1]
        current_time = datetime.now()
        last_email_time = datetime.strptime(last_line.split("__").__getitem__(1), "%Y-%m-%d %H:%M:%S.%f")
        diff = current_time - last_email_time
        diff_minutes = (diff.days * 24 * 60) + (diff.seconds / 60)
        if diff_minutes < 60:
            email_sent = True
    except Exception as error:
        email_sent = False
    return email_sent


if __name__ == "__main__":
    root_url = sys.argv[1]
    root_component_id = sys.argv[2]
    challenge_uri()
