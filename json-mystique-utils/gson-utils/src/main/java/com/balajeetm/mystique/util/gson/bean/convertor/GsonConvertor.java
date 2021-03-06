/*
 * Copyright (c) Balajee TM 2016.
 * All rights reserved.
 * License -  @see <a href="http://www.apache.org/licenses/LICENSE-2.0"></a>
 */

/*
 * Created on 25 Aug, 2016 by balajeetm
 * http://www.balajeetm.com
 */
package com.balajeetm.mystique.util.gson.bean.convertor;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.balajeetm.mystique.util.gson.bean.lever.JsonLever;
import com.balajeetm.mystique.util.json.convertor.JsonConvertor;
import com.balajeetm.mystique.util.json.error.ConvertorException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.Getter;

/**
 * The Class GsonConvertor.
 *
 * @author balajeetm
 */
/**
 * @author balajeetm
 *
 */
@Component
public class GsonConvertor implements JsonConvertor {

	/**
	 * Gets the gson.
	 *
	 * @return the gson
	 */

	/**
	 * Gets the gson.
	 *
	 * @return the gson
	 */
	@Getter
	private Gson gson;

	/**
	 * Gets the gson builder.
	 *
	 * @return the gson builder
	 */

	/**
	 * Gets the gson builder.
	 *
	 * @return the gson builder
	 */
	@Getter
	private GsonBuilder gsonBuilder;

	/** The json lever. */
	@Autowired
	private JsonLever jsonLever;

	/**
	 * Instantiates a new gson convertor.
	 */
	private GsonConvertor() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat(DateFormat.LONG, DateFormat.LONG);
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				Date date = null;
				if (null != json && json.isJsonPrimitive()) {
					date = new Date(json.getAsJsonPrimitive()
							.getAsLong());
				}
				return date;
			}

		});
		gsonBuilder.registerTypeAdapter(XMLGregorianCalendar.class, new JsonSerializer<XMLGregorianCalendar>() {

			@Override
			public JsonElement serialize(XMLGregorianCalendar src, Type typeOfSrc, JsonSerializationContext context) {
				Date date = null;
				if (null != src) {
					date = src.toGregorianCalendar()
							.getTime();
				}
				return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date));
			}
		});
		updateGson();
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		Creator.INSTANCE = this;
	}

	/**
	 * Update gson.
	 */
	public void updateGson() {
		gson = gsonBuilder.create();
	}

	/**
	 * Register type adapter.
	 *
	 * @param adapters the adapters
	 */
	public void registerTypeAdapter(GsonTypeAdapter... adapters) {
		if (!ArrayUtils.isEmpty(adapters)) {
			for (GsonTypeAdapter gsonTypeAdapter : adapters) {
				gsonBuilder.registerTypeAdapter(gsonTypeAdapter.getType(), gsonTypeAdapter.getAdapter());
			}
			updateGson();
		}
	}

	/**
	 * Gets the single instance of GsonConvertor.
	 *
	 * @return single instance of GsonConvertor
	 */
	public static JsonConvertor getInstance() {
		return Creator.INSTANCE;
	}

	// Efficient Thread safe Lazy Initialization
	// works only if the singleton constructor is non parameterized
	/**
	 * The Class Creator.
	 */
	private static class Creator {

		/** The instance. */
		public static JsonConvertor INSTANCE = new GsonConvertor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserialize(java.
	 * lang.String, java.lang.Class)
	 */
	@Override
	public <T> T deserialize(String jsonString, Class<T> pojoType) throws ConvertorException {
		try {
			return gson.fromJson(jsonString, pojoType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserialize(java.
	 * lang.Object, java.lang.Class)
	 */
	@Override
	public <T> T deserialize(Object object, Class<T> pojoType) throws ConvertorException {
		try {
			JsonElement jsonElement = getJsonElement(object);
			return gson.fromJson(jsonElement, pojoType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserialize(java.
	 * io.InputStream, java.lang.Class)
	 */
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> pojoType) throws ConvertorException {
		try {
			return gson.fromJson(new InputStreamReader(inputStream), pojoType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#serialize(java.
	 * lang.Object)
	 */
	@Override
	public String serialize(Object pojo) throws ConvertorException {
		try {
			return gson.toJson(pojo);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserializeGroup(
	 * java.lang.String, java.lang.Class, java.lang.Class)
	 */
	@Override
	public <T> T deserializeGroup(String jsonString, Class<T> groupClass, Class<?> pojoType) throws ConvertorException {
		try {
			checkArgument(groupClass != null && pojoType != null);
			GsonParametrizedType parametrizedType = new GsonParametrizedType(groupClass.getName(), pojoType.getName());

			return gson.fromJson(jsonString, parametrizedType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserializeGroup(
	 * java.io.InputStream, java.lang.Class, java.lang.Class)
	 */
	@Override
	public <T> T deserializeGroup(InputStream inputStream, Class<T> groupClass, Class<?> pojoType)
			throws ConvertorException {
		try {
			checkArgument(groupClass != null && pojoType != null);
			GsonParametrizedType parametrizedType = new GsonParametrizedType(groupClass.getName(), pojoType.getName());

			return gson.fromJson(new InputStreamReader(inputStream), parametrizedType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserializeList(
	 * java.io.InputStream, java.lang.Class)
	 */
	@Override
	public <T> List<T> deserializeList(InputStream inputStream, Class<T> pojoType) throws ConvertorException {
		try {
			checkArgument(pojoType != null);
			GsonParametrizedType parametrizedType = new GsonParametrizedType(List.class.getName(), pojoType.getName());

			return gson.fromJson(new InputStreamReader(inputStream), parametrizedType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserializeList(
	 * java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> List<T> deserializeList(String jsonString, Class<T> pojoType) throws ConvertorException {
		try {
			checkArgument(pojoType != null);
			GsonParametrizedType parametrizedType = new GsonParametrizedType(List.class.getName(), pojoType.getName());

			return gson.fromJson(jsonString, parametrizedType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.futuresight.util.mystique.lever.ConvertorInterface#deserializeList(
	 * java.lang.Object, java.lang.Class)
	 */
	@Override
	public <T> List<T> deserializeList(Object object, Class<T> pojoType) throws ConvertorException {
		try {
			checkArgument(pojoType != null);
			JsonElement jsonElement = getJsonElement(object);
			GsonParametrizedType parametrizedType = new GsonParametrizedType(List.class.getName(), pojoType.getName());

			return gson.fromJson(jsonElement, parametrizedType);
		} catch (Exception e) {
			throw getConvertorException(e);
		}
	}

	/**
	 * Gets the convertor exception.
	 *
	 * @param e the exception
	 * @return the convertor exception
	 */
	private ConvertorException getConvertorException(Exception e) {
		return new ConvertorException(e);
	}

	/**
	 * Gets the json element from a Java POJO.
	 *
	 * @param obj the obj
	 * @return the json element
	 */
	private JsonElement getJsonElement(Object obj) {
		JsonElement jsonElement = (obj instanceof JsonElement) ? (JsonElement) obj : gson.toJsonTree(obj);
		return jsonLever.isNull(jsonElement) ? null : jsonElement;
	}
}
