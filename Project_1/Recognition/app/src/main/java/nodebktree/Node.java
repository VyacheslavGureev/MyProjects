/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodebktree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Vyacheslav
 */
public class Node implements Serializable
{
    private String value;
    private Map<Integer, Node> childs;

    public Node(String v)
    {
        this.value = v;
        this.childs = new HashMap<>();
    }

    public void add(String value)
    {
        int distance = getMetric(this.value, value);
        if (this.childs.containsKey(distance))
        {
            this.childs.get(distance).add(value);
        }
        else
        {
            this.childs.put(distance, new Node(value));
        }
    }

    public void search(String value, Set<String> resultSet)
    {
        int distance = getMetric(this.value, value);

        if (distance <= 1)
        {
            resultSet.add(this.value);
        }

        for (int i = Math.max(distance - 1, 1); i <= distance + 1; i++)
        {
            Node ch = this.childs.get(i);
            if (ch != null)
                ch.search(value, resultSet);
        }
    }

    public int getMetric(String s1, String s2)
    {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 1; i <= s1.length(); i++)
        {
            dp[i][0] = i;
        }

        for (int j = 1; j <= s2.length(); j++)
        {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++)
        {
            for (int j = 1; j <= s2.length(); j++)
            {
                dp[i][j] = Math.min(dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1), Math.min(dp[i][j - 1], dp[i - 1][j]) + 1);            }
        }
        return dp[s1.length()][s2.length()];
    }
}
