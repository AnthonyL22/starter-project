import json
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from smtplib import SMTP

""" function to alert/alarm based on team contacts
# TODO make the toaddrs key parametrized by uri contact
"""


def email_notify(message, status):
    fromaddr = 'anthonyl22@gmail.com'
    toaddrs = 'anthonyl22@gmail.com'
    msg = MIMEMultipart()
    msg['From'] = fromaddr
    msg['To'] = toaddrs
    msg['Subject'] = status

    body = message
    msg.attach(MIMEText(body, 'plain'))

    server = SMTP('smtphost.gmail.com:25')
    server.starttls()
    # server.login('you', 'password')
    text = msg.as_string()
    server.sendmail(fromaddr, toaddrs, text)
    server.quit()


def hipchat_notify(token, room, message, color='yellow', notify=False,
                   format='text', host='anthony.gmail.com'):
    """Send notification to a HipChat room via API version 2
    Parameters
    ----------
    token : str
        HipChat API version 2 compatible token (room or user token)
    room: str
        Name or API ID of the room to notify
    message: str
        Message to send to room
    color: str, optional
        Background color for message, defaults to yellow
        Valid values: yellow, green, red, purple, gray, random
    notify: bool, optional
        Whether message should trigger a user notification, defaults to False
    format: str, optional
        Format of message, defaults to text
        Valid values: text, html
    host: str, optional
        Host to connect to, defaults to api.hipchat.com
    """

    if len(message) > 10000:
        raise ValueError('Message too long')
    if format not in ['text', 'html']:
        raise ValueError("Invalid message format '{0}'".format(format))
    if color not in ['yellow', 'green', 'red', 'purple', 'gray', 'random']:
        raise ValueError("Invalid color {0}".format(color))
    if not isinstance(notify, bool):
        raise TypeError("Notify must be boolean")

    url = "https://{0}/v2/room/{1}/notification".format(host, room)
    headers = {'Content-type': 'application/json', 'Authorization': "Bearer " + token}
    payload = {
        'message': message,
        'notify': notify,
        'message_format': format,
        'color': color
    }
    r = requests.post(url, data=json.dumps(payload), headers=headers)
    r.raise_for_status()


if __name__ == "__main__":
    email_notify()
    hipchat_notify()
