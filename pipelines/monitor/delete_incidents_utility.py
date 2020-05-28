import sys

import get_incidents as get_incidents

root_component_id = -1


######################################################################
# Delete all incidents for a particular component_id
######################################################################
def delete_incidents_by_component_id():
    incidents_to_delete_list = get_incidents.get_incident_list_by_component_id(int(root_component_id))

    answer = raw_input("Are you sure you'd like to DELETE {0!s} Incidents for component {1!s}? ".
                       format(incidents_to_delete_list.__len__(), root_component_id))
    if answer.__contains__('y'):
        get_incidents.delete_incident_list(incidents_to_delete_list)
        print('Finished Deleting {0!s} Incidents'.format(incidents_to_delete_list.__len__()))
    else:
        print('Skipping Deletion of {0!s} Incidents'.format(incidents_to_delete_list.__len__()))


if __name__ == "__main__":
    root_component_id = sys.argv[1]
    delete_incidents_by_component_id()
