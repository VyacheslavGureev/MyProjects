import csv


class readTrainingData:


    def readTrainingData(
            filenameDict,
            filenameSmallDict,
            filenameVal,
            encoding,
            language,
            delimiter):

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

        with open(filenameDict, encoding = encoding) as file:
            # Создаем объект reader, указываем символ-разделитель ","
            file_reader = csv.reader(file, delimiter = delimiter)

            skipFirstLine = True

            for row in file_reader:
                if skipFirstLine:
                    skipFirstLine = False
                    continue
                d[row[0]] = {
                            "n": row[1], "nn": row[2],
                            "i": row[3], "ni": row[4],
                            "obs": row[5], "nobs": row[6],
                            "th": row[7], "nth": row[8]}

        with open(filenameSmallDict, encoding=encoding) as file:
            # Создаем объект reader, указываем символ-разделитель ","
            file_reader = csv.reader(file, delimiter=delimiter)

            skipFirstLine = True

            for row in file_reader:
                if skipFirstLine:
                    skipFirstLine = False
                    continue
                uw[row[0]] = row[1]

        with open(filenameVal, encoding=encoding) as file:
            # Создаем объект reader, указываем символ-разделитель ","
            file_reader = csv.reader(file, delimiter=delimiter)

            skipFirstLine = True

            for row in file_reader:

                if skipFirstLine:
                    skipFirstLine = False
                    continue

                n = row[0]
                nn = row[1]

                i = row[2]
                ni = row[3]

                obs = row[4]
                nobs = row[5]

                th = row[6]
                nth = row[7]

                w = row[8]

        return [
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