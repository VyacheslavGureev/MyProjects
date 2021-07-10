import normalization


class processingMsg:


    def processingMsg (msg, trdata, lan):

        msg_tokens = normalization.Normalization.stringPerforming(msg, lan)

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

        resv = {}

        resv["normal"] = normalization.Normalization.get_result(d, n, nn, 'n', 'nn', w, uw, msg_tokens)
        resv["insult"] = normalization.Normalization.get_result(d, i, ni, 'i', 'ni', w, uw, msg_tokens)
        resv["obscenity"] = normalization.Normalization.get_result(d, obs, nobs, 'obs', 'nobs', w, uw, msg_tokens)
        resv["threat"] = normalization.Normalization.get_result(d, th, nth, 'th', 'nth', w, uw, msg_tokens)

        return resv