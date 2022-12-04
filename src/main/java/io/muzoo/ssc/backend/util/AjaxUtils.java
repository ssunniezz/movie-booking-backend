package io.muzoo.ssc.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class AjaxUtils {

    public static String convertToString(Object objectValue){
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try{
            mapper.writeValue(writer,objectValue);
            return writer.toString();
        } catch (IOException e){
            return "[bad object/conversion]";
        }

    }
}
