import time
import re

import normalization
import unloadTrainingData


class ParseTrainingData:


    def parseTrainingData(
        filenameData,
        encodingData,
        languageData,
        delimiterData,
        modeData,

        filenameUnldDataDicts,
        filenameSmallDictionary,
        filenameUnldDataValues,
        encodingUnldData,
        languageUnldData,
        delimiterUnldData,
        lineterminatorUnldData,
        modeUnldData):

        with open(
                filenameData,
                modeData,
                encoding = encodingData)\
                as file:

            w = 0
            uw = {}

            n = 0
            nn = 0

            i = 0
            ni = 0

            obs = 0
            nobs = 0

            th = 0
            nth = 0

            d = {}

            checkFlag = 0
            rows = 0
            rowsTime = 0

            commonStartTime = time.time()

            for line in file:
                str = line.partition(' ')[2]
                toxicFlag = line.partition(' ')[0]

                tokens = normalization.Normalization.stringPerforming(str, languageData)

                w += len(tokens)

                mat = re.findall('(__label__NORMAL)|(__label__INSULT)|(__label__OBSCENITY)|(__label__THREAT)', toxicFlag)
                matches = []
                for m in mat:
                    for j in m:
                      if j != "":
                          matches.append(j)

                if "__label__NORMAL" in matches:
                    n += len(tokens)
                    for wrd in tokens:
                        uw = normalization.Normalization.incDict(uw, wrd)
                        if d.get(wrd):
                            d[wrd]["n"] += 1
                        else:
                            d[wrd] = {
                                        "n": 1, "nn": 0,
                                        "i": 0, "ni": 0,
                                        "obs": 0, "nobs": 0,
                                        "th": 0, "nth": 0
                                      }
                else:
                    nn += len(tokens)
                    for wrd in tokens:
                        uw = normalization.Normalization.incDict(uw, wrd)
                        if d.get(wrd):
                            d[wrd]["nn"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 1,
                                "i": 0, "ni": 0,
                                "obs": 0, "nobs": 0,
                                "th": 0, "nth": 0
                            }

                if "__label__INSULT" in matches:
                    i += len(tokens)
                    for wrd in tokens:
                        if d.get(wrd):
                            d[wrd]["i"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 0,
                                "i": 1, "ni": 0,
                                "obs": 0, "nobs": 0,
                                "th": 0, "nth": 0
                            }
                else:
                    ni += len(tokens)
                    for wrd in tokens:
                        if d.get(wrd):
                            d[wrd]["ni"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 0,
                                "i": 0, "ni": 1,
                                "obs": 0, "nobs": 0,
                                "th": 0, "nth": 0
                            }

                if "__label__OBSCENITY" in matches:
                    obs += len(tokens)
                    for wrd in tokens:
                        if d.get(wrd):
                            d[wrd]["obs"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 0,
                                "i": 0, "ni": 0,
                                "obs": 1, "nobs": 0,
                                "th": 0, "nth": 0
                            }
                else:
                    nobs += len(tokens)
                    for wrd in tokens:
                        if d.get(wrd):
                            d[wrd]["nobs"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 0,
                                "i": 0, "ni": 0,
                                "obs": 0, "nobs": 1,
                                "th": 0, "nth": 0
                            }

                if "__label__THREAT" in matches:
                    th += len(tokens)
                    for wrd in tokens:
                        if d.get(wrd):
                            d[wrd]["th"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 0,
                                "i": 0, "ni": 0,
                                "obs": 0, "nobs": 0,
                                "th": 1, "nth": 0
                            }
                else:
                    nth += len(tokens)
                    for wrd in tokens:
                        if d.get(wrd):
                            d[wrd]["nth"] += 1
                        else:
                            d[wrd] = {
                                "n": 0, "nn": 0,
                                "i": 0, "ni": 0,
                                "obs": 0, "nobs": 0,
                                "th": 0, "nth": 1
                            }

                checkFlag += 1
                rows += 1

                if checkFlag % 100 == 0:
                    if checkFlag == 100:
                        print(
                            f"Обработано: {rows} строк, время обработки {100} последних строк: {time.time() - commonStartTime} сек, общее время: {time.time() - commonStartTime} сек")
                        v = 100/(time.time() - commonStartTime)
                        t = (248290/v) - (time.time() - commonStartTime)
                        print(
                            f"Осталось: {248290 - rows} строк, скорость обработки: {100} последних строк: {v} стр/сек, оcталось времени: {t} сек ({t/3600} часов)")
                    else:
                        print(
                            f"Обработано: {rows} строк, время обработки {100} последних строк: {time.time() - rowsTime} сек, общее время: {time.time() - commonStartTime} сек")
                        v = 100 / (time.time() - rowsTime)
                        t = (248290 / v) - (time.time() - commonStartTime)
                        print(
                            f"Осталось: {248290 - rows} строк, скорость обработки {100} последних строк: {v} стр/сек, оcталось времени: {t} сек ({t/3600} часов)")
                    rowsTime = time.time()

        trdataUnldData = [
                            w,
                            uw,

                            n,
                            nn,

                            i,
                            ni,

                            obs,
                            nobs,

                            th,
                            nth,

                            d]

        unloadTrainingData.unloadTrainingData.unload(filenameUnldDataDicts,
                                                     filenameSmallDictionary,
                                                     filenameUnldDataValues,
                                                     encodingUnldData,
                                                     languageUnldData,
                                                     delimiterUnldData,
                                                     lineterminatorUnldData,
                                                     modeUnldData,
                                                     trdataUnldData)

        return "done"