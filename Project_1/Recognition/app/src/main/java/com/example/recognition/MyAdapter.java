package com.example.recognition;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
    ArrayList<RecycleViewLine> rvl;

    MyViewHolder mvh;

    public MyAdapter()
    {
        rvl = new ArrayList<>();
    }

    // создаём пустое представление и оборачиваем его в класс (джава-объект) - вью холдер
    // когда требуется элементы для вью холдера, этот метод создаёт их
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        int listItemId = R.layout.recycle_view_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(listItemId, parent, false);
        mvh = new MyViewHolder(view);
        return mvh;
    }

    // заполняем пустые представления данными (это происходит именно при прокрутке рецайкл вью,
    // потому что рецайкл вью создаёт ограниченное количество пустых представлений,
    // которые потом повторно многократно использует, заполняя различными данными в процессе прокрутки.
    // Таким образом достигается значительная оптимизация, которую нельзя было достичь при использовании лист вью
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        // в позиции хранится номер нового представления
        holder.priceField.setText(rvl.get(position).name);
        holder.viewHolderQuantities.setText(rvl.get(position).quantity);
        holder.costs.setText(rvl.get(position).cost);

        //getValue(rvl.get(position));
    }

    @Override
    public int getItemCount()
    {
        return rvl.size();
    }

    // в этом классе происходит поиск ключевых элементов в xml файле, который используется как макет для элемента рецайкл вью
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        EditText priceField;
        EditText viewHolderQuantities;
        EditText costs;
        public MyViewHolder(View itemView)
        {
            super(itemView);

            priceField = itemView.findViewById(R.id.name);
            viewHolderQuantities = itemView.findViewById(R.id.quantity);
            costs = itemView.findViewById(R.id.cost);

            priceField.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    rvl.get(getAdapterPosition()).name = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s)
                {

                }
            });

            viewHolderQuantities.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    rvl.get(getAdapterPosition()).quantity = s.toString();
                    getValue(rvl.get(getAdapterPosition()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            costs.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    rvl.get(getAdapterPosition()).cost = s.toString();
                    getValue(rvl.get(getAdapterPosition()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    /*private void getValue(RecycleViewLine r)
    {
        String count;
        String value;

        count = r.quantity;
        value = r.cost;

        String regExCount = "[0-9]+";
        String regExValue = "((([0-9][0-9][0-9] |[0-9][0-9] |[0-9] )*[0-9]{3}|[0-9]|[1-9]*[0-9])[.,\\- ][0-9]{2}|(([0-9][0-9][0-9] |[0-9][0-9] |[0-9] )*[0-9]{3}|[0-9]|[1-9]*[0-9])[.,\\- ][0-9](?![0-9]))";

        Pattern patternCount = Pattern.compile(regExCount);
        Matcher matcherCount = patternCount.matcher(count);

        Pattern patternValue = Pattern.compile(regExValue);
        Matcher matcherValue = patternValue.matcher(value);

        if (matcherCount.find() && matcherValue.find())
        {
            count = count.substring(matcherCount.start(), matcherCount.end());
            value = value.substring(matcherValue.start(), matcherValue.end());

            count = count.replaceAll("\\s","");
            value = value.replaceAll("\\s","");

            float val = 0;

            float c = 0;
            float v = 0;

            String resCount = count;

            String resValue = "";

            for (int i = 0; i < value.length(); i++)
            {
                if (Character.isDigit(value.charAt(i)))
                {
                    resValue = resValue + value.charAt(i);
                }
            }

            float a = Float.parseFloat(resCount.substring(0));
            float d = 0;

            c = a + d/100;

            a = Float.parseFloat(resValue.substring(0, resValue.length()-2));
            d = Float.parseFloat(resValue.substring(resValue.length() - 2, resValue.length()));

            v = a + d/100;

            val = c * v;

            r.tsena = val;
        }
    }*/

    private void getValue(RecycleViewLine r)
    {
        String quan;
        String cost;

        quan = r.quantity;
        cost = r.cost;

        quan = quan.replaceAll("\\s","");
        cost = cost.replaceAll("\\s","");

        String regExIntPart = "[0-9]+";

        Pattern patternIntPart = Pattern.compile(regExIntPart);

        Matcher matcherIntPartQuan = patternIntPart.matcher(quan);
        Matcher matcherIntPartCost = patternIntPart.matcher(cost);

        if (matcherIntPartQuan.find() && matcherIntPartCost.find())
        {
            int quanStart = matcherIntPartQuan.start();
            int costStart = matcherIntPartCost.start();

            int quanEnd = matcherIntPartQuan.end();
            int costEnd = matcherIntPartCost.end();

            String regExFractPart = "[.,\\-][0-9]+";

            Pattern patternFractPart = Pattern.compile(regExFractPart);

            String quanNext = quan.substring(quanEnd);
            String costNext = cost.substring(costEnd);

            Matcher matcherFractPartQuan = patternFractPart.matcher(quanNext);
            Matcher matcherFractPartCost = patternFractPart.matcher(costNext);

            double val = 0;
            double q = 0;
            double c = 0;

            double i = 0;
            double f = 0;

            int indicator = 0;

            if (matcherFractPartQuan.find() && matcherFractPartCost.find())
            {
                i = Float.parseFloat(quan.substring(quanStart, quanEnd));
                f = Float.parseFloat(quanNext.substring(matcherFractPartQuan.start() + 1, matcherFractPartQuan.end()));

                String len = quanNext.substring(matcherFractPartQuan.start() + 1, matcherFractPartQuan.end());

                indicator = len.length();

                q = i + f/(Math.pow(10, indicator));

                i = Float.parseFloat(cost.substring(costStart, costEnd));
                f = Float.parseFloat(costNext.substring(matcherFractPartCost.start() + 1, matcherFractPartCost.end()));

                len = costNext.substring(matcherFractPartCost.start() + 1, matcherFractPartCost.end());

                indicator = len.length();

                c = i + f/(Math.pow(10, indicator));

                r.commonValue = q * c;
            }
            else if (!matcherFractPartQuan.find() && matcherFractPartCost.find())
            {
                i = Float.parseFloat(quan.substring(quanStart, quanEnd));

                q = i;

                i = Float.parseFloat(cost.substring(costStart, costEnd));
                f = Float.parseFloat(costNext.substring(matcherFractPartCost.start() + 1, matcherFractPartCost.end()));

                String len = costNext.substring(matcherFractPartCost.start() + 1, matcherFractPartCost.end());

                indicator = len.length();

                c = i + f/(Math.pow(10, indicator));

                r.commonValue = q * c;
            }
            else if (matcherFractPartQuan.find() && !matcherFractPartCost.find())
            {
                i = Float.parseFloat(quan.substring(quanStart, quanEnd));
                f = Float.parseFloat(quanNext.substring(matcherFractPartQuan.start() + 1, matcherFractPartQuan.end()));

                String len = quanNext.substring(matcherFractPartQuan.start() + 1, matcherFractPartQuan.end());

                indicator = len.length();

                q = i + f/(Math.pow(10, indicator));

                i = Float.parseFloat(cost.substring(costStart, costEnd));

                c = i;

                r.commonValue = q * c;
            }
            else if (!matcherFractPartQuan.find() && !matcherFractPartCost.find())
            {
                i = Float.parseFloat(quan.substring(quanStart, quanEnd));

                q = i;

                i = Float.parseFloat(cost.substring(costStart, costEnd));

                c = i;

                r.commonValue = q * c;
            }
        }
    }

    private ArrayList<Float> extractPrices(ArrayList<String> prices)
    {
        ArrayList<Float> listOfFloat = new ArrayList<Float>();
        String num = "";
        String res = "";
        for (int i = 0; i < prices.size(); i++)
        {
            num = prices.get(i);
            for (int j = 0; j < num.length(); j++)
            {
                if (Character.isDigit(num.charAt(j)))
                {
                    res = res + num.charAt(j);
                }
            }
            float a = Float.parseFloat(res.substring(0, res.length()-2));
            float d = Float.parseFloat(res.substring(res.length() - 2, res.length()));
            listOfFloat.add(a + d/100);
            res = "";
        }
        return listOfFloat;
    }
}
