import nltk

import os

from flask import Flask, jsonify, request, render_template
from flask_restful import Api, abort
import readTrainingData
import processingMsg

nltk.download('punkt')
nltk.download('stopwords')

app = Flask(__name__)
api = Api(app)

data = dict({"message" : None, "toxic_flag" : None, "training_data" : None})


@app.route('/')
def home():
    return render_template("index.html")


@app.route('/', methods = ['POST', 'GET'])
def process_msg():
    data["message"] = request.form['TEXT']
    if data["training_data"] == None:
        bigDictionaryOutFile = "training_data/trdt_big_dictionary.csv"
        smallDictionaryOutFile = "training_data/trdt_small_dictionary.csv"
        valuesOutFile = "training_data/trdt_values.csv"
        data['training_data'] = readTrainingData.readTrainingData.readTrainingData(
            bigDictionaryOutFile,
            smallDictionaryOutFile,
            valuesOutFile,
            "utf-8",
            "russian",
            ",")
    data["toxic_flag"] = processingMsg.processingMsg.processingMsg(data["message"], data["training_data"], "russian")
    toxic_flag = True if (data.get("toxic_flag")).get("normal") == False and ((data.get("toxic_flag")).get("insult") == True or (data.get("toxic_flag")).get("obscenity") == True or (data.get("toxic_flag")).get("threat") == True) else False
    return render_template("index.html", status = "Success!", msg = data['message'], toxic_flag = toxic_flag)


@app.route('/post', methods = ['POST'])
def post():
    print(request.headers)
    print(request.data)
    print(request.args)
    if not request.json or not 'message' in request.json:
        return abort(400)
    data['message'] = request.json.get('message', '')
    if data['training_data'] == None:
        bigDictionaryOutFile = "training_data/trdt_big_dictionary.csv"
        smallDictionaryOutFile = "training_data/trdt_small_dictionary.csv"
        valuesOutFile = "training_data/trdt_values.csv"
        data['training_data'] = readTrainingData.readTrainingData.readTrainingData(
                    bigDictionaryOutFile,
                    smallDictionaryOutFile,
                    valuesOutFile,
                    "utf-8",
                    "russian",
                    ",")
    data["toxic_flag"] = processingMsg.processingMsg.processingMsg(data["message"], data["training_data"], "russian")
    toxic_flag = True if (data.get("toxic_flag")).get("normal") == False and (
            (data.get("toxic_flag")).get("insult") == True or (data.get("toxic_flag")).get("obscenity") == True or (
        data.get("toxic_flag")).get("threat") == True) else False
    return jsonify({'toxic_status': toxic_flag}), 201


if __name__ == '__main__':
    env_port = int(os.environ.get('PORT', 5000))
    app.run(debug = False, port = env_port)