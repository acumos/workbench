from traitlets import Unicode

from jupyterhub.auth import Authenticator
import configparser
import json
import requests
import os, sys
from tornado import gen

config = configparser.ConfigParser()
config.read('/srv/jupyterhub/secrets/cds.env')

class AcumosAuthenticator(Authenticator):

    @gen.coroutine
    def authenticate(self, handler, data):
        cds_url = config.get('CDSSection', 'cmndatasvc.url')+'/user/login'
        cds_user = config.get('CDSSection', 'cmndatasvc.user')
        cds_password = config.get('CDSSection', 'cmndatasvc.password')
        username = data['username']
        data = {"name" : username, "pass" : data['password']}
        data_json = json.dumps(data)
        headers = {'Content-type': 'application/json'}
        response = requests.post(cds_url, data=data_json, headers=headers,auth=(cds_user, cds_password))
        json_data = json.loads(response.text)
        if json_data.get('authToken')   :
            return username
        else:
            return None
