from bs4 import BeautifulSoup
import requests
import urllib3.request
import time
import csv
#url = "https://www.geeksforgeeks.org/reverse-level-order-traversal/"

base_file_destination = "/Users/satya/Desktop/Personal/CodeDataset/"
metadata_file = "/Users/satya/Desktop/Personal/metadata.csv"
gfg_lookup_file = "/Users/satya/Desktop/Personal/gfg_lookup.csv"
codeforces_lookup_file = "/Users/satya/Desktop/Personal/codeforces_lookup.csv"


def parse_gfg_csv():
    with open("/Users/satya/Desktop/Personal/gfg_data.csv", 'r') as f:
        lis = [line.split('\r') for line in f]
        print(lis)
        first = True
        for item in lis:
            if(first):
                first = False
            else:
                information = item[0].split(',')
                url = information[0]
                complexity = []
                for y in range(1,len(information)):
                    if(information[y] != '' and information[y] != '\n'):
                        complexity.append(information[y])
                print(complexity)
                code_counter = download_code(url,complexity,code_counter)
                



def download_code(url,complexity,code_counter):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")
    tabs = soup.findAll("h2", { "class" : "tabtitle" }) 
    indexes = []
    #index = 0
    for i, tab in enumerate(tabs):
        if(tab.contents[0] == 'Java'):
            #index = i
            indexes.append(i)
    #index
    codes = soup.findAll("td", { "class" : "code" }) 
    print(indexes)
    code_index = 1
    complexity_index = 0
    for index in indexes:
        print('yahan')
        if(complexity_index == len(complexity)):
            break
        print('yahan nahi')


        lines = codes[index].findChildren("div", recursive=False)[0].findChildren("div", recursive=False)
        #print(lines)
        content = []
        for line in lines:
            #content.append((line.contents)[0].contents[0])
            content.append(line.contents)
            
        output = ''
        for line in content:
            #print(line)
            for l in line:
                if(str(type(l)) == "<class 'bs4.element.Tag'>"):
                    final = ''

                    for c in l.contents:
                        final += c
                    final.encode("utf-8")
                    #print(final)
                    output += "\n" + final
        output.encode("utf-8")
        #print(output)
        print("*************************")
        download_file_name = base_file_destination + str(code_counter)+'.java'
        with open(download_file_name,'w') as f:
            f.write(output)
        f.close()
        with open(metadata_file,'a') as output:
            out = csv.writer(output)
            print("index complexity" + complexity[complexity_index])
            out.writerow([download_file_name,complexity[complexity_index]])
        output.close()

        with open(gfg_lookup_file, 'a') as lookupfile:
            lookupfilewriter = csv.writer(lookupfile)
            lookupfilewriter.writerow([code_counter, url,code_index])
        lookupfile.close()

        code_counter +=1
        complexity_index+=1
        code_index += 1
    return code_counter

code_counter = 1

with open("/Users/satya/Desktop/Personal/gfg_data.csv", 'r') as f:
    lis = [line.split('\r') for line in f]
    print(lis)
    first = True
    for item in lis:
        if(first):
            first = False
        else:
            information = item[0].split(',')
            url = information[0]
            complexity = []
            for y in range(1,len(information)):
                if(information[y] != '' and information[y] != '\n'):
                    complexity.append(information[y])
            print(complexity)
            try:
                code_counter = download_code(url,complexity,code_counter)
            except:
                print("exception for " + url)



print("*****gfg_done****")

def download_code_from_codeforce(url, file_location):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")
    code = soup.find("pre", { "id" : "program-source-text", "class" : "lang-java"}) 
    if type(code) != "NoneType":
        with open(file_location, "w") as f:
            f.write(code.text)
        f.close()


with open('/Users/satya/Desktop/Personal/codeforces_data.csv','r') as codeforces_data:
    data_list = [line.split('\r') for line in codeforces_data]
    first = True
    for item in data_list:
        print(item)
        if(first):
            first = False
        else:
            information = item[0].split(',')
            print(information)
            submissionId = information[0]
            complexity = information[1]
            contestId = information[2]
            problemIndex = information[3]
            url = "https://codeforces.com/contest/" + str(contestId) + "/submission/" + str(submissionId)
            file_location = base_file_destination + str(code_counter)+'.java'
            try:
                download_code_from_codeforce(url,file_location)
                with open(metadata_file,'a') as output:
                    out = csv.writer(output)
                    print("index complexity" + complexity)
                    out.writerow([file_location,complexity])

                with open(codeforces_lookup_file, 'a') as lookupfile:
                    lookupfilewriter = csv.writer(lookupfile)
                    lookupfilewriter.writerow([code_counter, submissionId, file_location, contestId, problemIndex])            
                code_counter += 1
            except:
                print("exception for " + url)

    for x in range(1, len(data_list)):
        current_data = data_list[x]
        information = current_data.split(',')

