package com.dissidia986.util;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WtUtils {
	
	
	public static ObjectMapper getMapper(){
		ObjectMapper _mapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		_mapper.getDeserializationConfig().with(dateFormat);
		_mapper.getSerializationConfig().with(dateFormat);
		//TODO 鍏佽JSON涓茬殑key涓嶇敤鍙屽紩鍙峰寘鎷�
		_mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// to allow C/C++ style comments in JSON (non-standard, disabled by default)
		_mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		// to allow (non-standard) unquoted field names in JSON:
		_mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// to allow use of apostrophes (single quotes), non standard
		_mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

		// JsonGenerator.Feature for configuring low-level JSON generation:

		// to force escaping of non-ASCII characters:
		_mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		return _mapper;
	}
	
	public static String mergeTemplate(VelocityEngine velocityEngine, String templateLocation,Map<String,Object> model){
		model.put("DateTool", new DateTool());
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation,"UTF-8", model);
		System.err.println(text);
		return text;
	}
}
