import challenge_api as challenge
import set_components as components

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
    uri = "https://localhost/oauth2/info"
    component_id = 1
    verify_endpoint(False, uri, component_id)

    uri = "https://localhost/oauth2/info"
    component_id = 2
    verify_endpoint(False, uri, component_id)

    uri = "https://localhost/oauth2/info"
    component_id = 3
    verify_endpoint(False, uri, component_id)

    uri = "https://localhost/oauth2/info"
    component_id = 4
    verify_endpoint(False, uri, component_id)


"""
# Verify single endpoint and log status & URI
"""


def verify_endpoint(siteminder_protected, uri, component_id):
    challenge.sm_protected = siteminder_protected
    challenge.root_url = uri
    challenge.root_component_id = component_id
    current_component_status = challenge.challenge_uri()
    print "Status={0!s} :: Checking '{1!s}'".format(current_component_status, uri)
    components.root_component_id = component_id
    components.root_component_status = current_component_status
    components.update_component()


if __name__ == '__main__':
    monitoring_endpoints()
