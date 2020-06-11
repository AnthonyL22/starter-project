import challenge_api as challenge
import set_components as components

APP_KEY = ''
AEM_KEY = ''
NON_PROD_APP_KEY = ''

BACKEND_ENV = ''
MESOS_ENV = ''


"""
Continuous Quality Monitoring Tool

    This list contains the attributes necessary to push changes to the 
    cachet - status_page located at localhost.

    Args:
        uri: A mix of RESTful endpoints and classic URLs
        component_id: The unique identifier given to the URI in the cachethq configuration/settings
        email_contact: each uri has an associated escalation contact

    Returns:

    Raises:

"""


def monitoring_endpoints():
    check_endponts()
    # add future endpoint groups here


def check_endponts():

    uri = "https://{0!s}actuator/health".format(MESOS_ENV)
    component_id = 1
    verify_endpoint(uri, component_id)

    uri = "https://{0!s}/login".format(BACKEND_ENV)
    component_id = 9
    headers = {'Content-Type': 'text/plain',
               'AppKey': NON_PROD_APP_KEY}
    body = {'username': 'anthony@getnada.com',
            'password': 'password1'}
    result = verify_endpoint_headers(uri, component_id, headers, True, body, None)

    authentication = {'access_token': result[0],
                      'vds_id': result[1]}

    uri = "https://aws-{0!s}.api.rccl.com/agreement".format(BACKEND_ENV)
    component_id = 10
    headers = {'appkey': APP_KEY}
    verify_endpoint_headers(uri, component_id, headers, False, "", authentication)

    uri = "https://aws-{0!s}.api.rccl.com/v1/profileBookings/searchAddGetProfileBookings".format(BACKEND_ENV)
    component_id = 11
    headers = {'appkey': APP_KEY,
               'access-token': authentication['access_token'],
               'content-type': 'application/json'}
    body = {'flagAsPrimaryBooking': 'false',
            'header': {'brand': 'C', 'channel': 'web', 'locale': 'en-US'}}
    verify_endpoint_headers(uri, component_id, headers, True, body, authentication)


"""
# Verify single endpoint and log status & URI
"""


def verify_endpoint(uri, component_id):
    challenge.root_url = uri
    challenge.root_component_id = component_id
    current_component_status = challenge.challenge_uri()
    print "Status={0!s} :: Checking '{1!s}'".format(current_component_status, uri)
    components.root_component_id = component_id
    components.root_component_status = current_component_status
    components.update_component()


def verify_endpoint_headers(uri, component_id, headers, is_post, body, authentication):
    challenge.root_url = uri
    challenge.headers = headers
    challenge.is_post = is_post
    challenge.body = body
    challenge.root_component_id = component_id
    current_component_status = challenge.challenge_uri()
    components.root_component_id = component_id
    components.root_component_status = current_component_status
    components.update_component()
    if isinstance(current_component_status, tuple) and len(current_component_status) > 1:
        print "Status={0!s} :: Checking '{1!s}'".format(current_component_status[0], uri)
        return current_component_status[1], current_component_status[2]
    else:
        print "Status={0!s} :: Checking '{1!s}'".format(current_component_status, uri)


if __name__ == '__main__':
    monitoring_endpoints()
