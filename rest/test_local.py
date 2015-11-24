import sys
import json
import urllib
import urllib2

port = sys.argv[1] if len(sys.argv) >= 2 else '51377'
url = sys.argv[2] if len(sys.argv) >= 3 else 'gqrest/fisher/search_sorted'

params = {
   'species': 'mm',
   'genes': '14081 14469 229898 229900 16145 12182 60440 19225 224796 12702 16169 56619 16176 17329 21939 19124 16181 99899 17472 21825 16068 58185 20555 18126 20304 15953 11988 15957 15958 58203 215900 100702 100038882 231655 54396 12266 20715 12524 16365 16362 20210 20723 100039796 60533 110454 108116 11898 55932 12494',
}

params = {
    'species': 'hs',
    'genes': '2180 81030 730249 91543 26253 6542 7057 163351 5272 5743 53405 115361 115362 9636 388646 683 942 10544 952 4283 9021 958 441168 10561 3659 29126 28232 2634 2635 718 6352 10068 3553 3557 3601 3433 3434 4843 445',
}

# params = {
#     'species': 'mm',
#     'genes': '18720 100502613 100043255 595141 14550'
# }
final_url = 'http://localhost:{}/{}?{}'.format(port, url, urllib.urlencode(params))
print "Open", final_url
#data = [x.strip().split('\t') for x in urllib2.urlopen(final_url).readlines()]
data = json.load(urllib2.urlopen(final_url))
print json.dumps(data[:30], indent=2)
