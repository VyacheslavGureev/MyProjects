import csv


class unloadTrainingData:


    def unload(filenameBigDictionary,
               filenameSmallDictionary,
               filenameValues,
               encoding,
               language,
               delimiter,
               lineterminator,
               mode,
               trdata):

        w = trdata[0]
        uw = trdata[1]

        n = trdata[2]
        nn = trdata[3]

        i = trdata[4]
        ni = trdata[5]

        obs = trdata[6]
        nobs = trdata[7]

        th = trdata[8]
        nth = trdata[9]

        d = trdata[10]

        with open(
                filenameBigDictionary,
                mode=mode,
                encoding=encoding
        ) \
                as w_file:

            fildNamesFirstLine = [
                                    "Слово",

                                    "n",
                                    "nn",

                                    "i",
                                    "ni",

                                    "obs",
                                    "nobs",

                                    "th",
                                    "nth"]

            file_writer = csv.DictWriter(
                w_file,
                delimiter=delimiter,
                lineterminator=lineterminator,
                fieldnames=fildNamesFirstLine)

            file_writer.writeheader()

            for word in d:
                file_writer.writerow({
                    "Слово": word,

                    "n": d[word]["n"],
                    "nn": d[word]["nn"],

                    "i": d[word]["i"],
                    "ni": d[word]["ni"],

                    "obs": d[word]["obs"],
                    "nobs": d[word]["nobs"],

                    "th": d[word]["th"],
                    "nth": d[word]["nth"]})

        with open(
                filenameSmallDictionary,
                mode=mode,
                encoding=encoding
        ) \
                as w_file:
            fildNamesFirstLine = [
                "Слово",
                "Сколько раз встретилось"]
            file_writer = csv.DictWriter(
                w_file,
                delimiter=delimiter,
                lineterminator=lineterminator,
                fieldnames=fildNamesFirstLine)
            file_writer.writeheader()
            for word in uw:
                file_writer.writerow({
                    "Слово": word,
                "Сколько раз встретилось": uw[word]})

        with open(
                filenameValues,
                mode=mode,
                encoding=encoding
        ) as w_file:

            fildNamesFirstLine = [
                "Слов в нормальных комментах (n)",
                "Слов в не нормальных комментах (nn)",

                "Слов в резких комментах (i)",
                "Слов в не резких комментах (ni)",

                "Слов в пошлых комментах (obs)",
                "Слов в не пошлых комментах (nobs)",

                "Слов в жёстких комментах (th)",
                "Слов в не жёстких комментах (nth)",

                "Всего слов во всех комментах (w)",
            ]

            file_writer = csv.DictWriter(
                w_file,
                delimiter=delimiter,
                lineterminator=lineterminator,
                fieldnames=fildNamesFirstLine
            )

            file_writer.writeheader()

            file_writer.writerow({
                "Слов в нормальных комментах (n)": n,
                "Слов в не нормальных комментах (nn)": nn,

                "Слов в резких комментах (i)": i,
                "Слов в не резких комментах (ni)": ni,

                "Слов в пошлых комментах (obs)": obs,
                "Слов в не пошлых комментах (nobs)": nobs,

                "Слов в жёстких комментах (th)": th,
                "Слов в не жёстких комментах (nth)": nth,

                "Всего слов во всех комментах (w)": w,
            })

        return "done"