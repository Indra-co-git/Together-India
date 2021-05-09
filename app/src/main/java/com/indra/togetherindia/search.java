package com.indra.togetherindia;

import android.text.style.AlignmentSpan;

import java.util.ArrayList;
import java.util.List;

public class search {


    public List<Entry> search_text(String text, List<Entry> search_on){
        List<Entry>output_list=new ArrayList<Entry>();
        text=text.toLowerCase();
        for (int i=0;i<search_on.size();i++){
            if(search_on.get(i).getName().toLowerCase().contains(text))
                output_list.add(search_on.get(i));
            else if(search_on.get(i).getRequirement().toLowerCase().contains(text))
                output_list.add(search_on.get(i));
            else if(search_on.get(i).getMobileNo().toLowerCase().contains(text))
                output_list.add(search_on.get(i));
            else if(search_on.get(i).getSeverity().toLowerCase().contains(text))
                output_list.add(search_on.get(i));
        }
        return output_list;
    }
}
