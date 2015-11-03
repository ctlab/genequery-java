import sys
import urllib
import urllib2

port = sys.argv[1] if len(sys.argv) >= 2 else '8081'
url = sys.argv[2] if len(sys.argv) >= 3 else 'gqrest/fisher/search_sorted'

params = {
    'species': 'mm',
    'genes': '14081 14469 229898 229900 16145 12182 60440 19225 224796 12702 16169 56619 16176 17329 21939 19124 16181 99899 17472 21825 16068 58185 20555 18126 20304 15953 11988 15957 15958 58203 215900 100702 100038882 231655 54396 12266 20715 12524 16365 16362 20210 20723 100039796 60533 110454 108116 11898 55932 12494',
}
final_url = 'http://localhost:{}/{}?{}'.format(port, url, urllib.urlencode({}))
print "Open", final_url
data = [x.strip().split('\t') for x in urllib2.urlopen(final_url).readlines()]

for gse, gpl, module_num, logP, empLogP, inters, modLen in data:
    print gse, gpl, empLogP, "{}/{}".format(inters, modLen)
