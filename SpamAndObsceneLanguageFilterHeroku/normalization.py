import nltk
import pymorphy2
import string
import re

import math

nltk.download('punkt')
nltk.download('stopwords')


class Normalization:


    @staticmethod
    def toLoverCase(str):
        return str.lower()


    @staticmethod
    def removeDigits(str):
        return re.sub(r'\d', '', str)


    @staticmethod
    def tokenization(str, language):
        from nltk.tokenize import sent_tokenize, word_tokenize
        res = word_tokenize(str, language = language)
        return res


    @staticmethod
    def removePunc(tokens):
        from nltk import word_tokenize
        punctuations = list(string.punctuation)
        punctuations.append("...")
        filteredTokens = []
        for token in tokens:
            if token not in punctuations:
                filteredTokens.append(token)
        return filteredTokens


    @staticmethod
    def deleteStopWords(tokens, language):
        from nltk.corpus import stopwords
        stopWords = stopwords.words(language)
        filteredTokens = []
        for token in tokens:
            if token not in stopWords:
                filteredTokens.append(token)
        return filteredTokens


    @staticmethod
    def normalization(tokens):
        morph = pymorphy2.MorphAnalyzer()
        normalTokens = []
        for token in tokens:
            normalToken = morph.parse(token)[0].normal_form
            normalTokens.append(normalToken)
        return normalTokens


    @staticmethod
    def stringPerforming(str, language):
        res = Normalization.toLoverCase(str)
        res = Normalization.removeDigits(res)
        res = Normalization.tokenization(res, language)
        res = Normalization.removePunc(res)
        res = Normalization.deleteStopWords(res, language)
        res = Normalization.normalization(res)
        return res


    @staticmethod
    def incDict(dictionary, key):
        if dictionary.get(key):
            dictionary[key] += 1
        else:
            dictionary[key] = 1
        return dictionary


    @staticmethod
    def get_result(d, t, nt, key, nkey, w, uw, msg):

        a = 0
        b = 0

        pt = int(t) / int(w)
        pnt = int(nt) / int(w)
        u = len(uw.keys())

        for word in msg:
            if d.get(word):
                x = (d.get(word)).get(key)
                pwit = (int(x) + 1) / (int(t) + u)
            else:
                pwit = (1) / (int(t) + u)
            a += math.log(pwit)
            if d.get(word):
                y = (d.get(word)).get(nkey)
                pwint = (int(y) + 1) / (int(nt) + u)
            else:
                pwint = (1) / (int(nt) + u)
            b += math.log(pwint)
        a += math.log(pt)
        b += math.log(pnt)
        return True if a >= b else False