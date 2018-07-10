package com.silita.utils.kafka;

import com.silita.model.Document;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class DocumentEncoder implements Serializer<Document> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public byte[] serialize(String topic, Document document) {
		if(null!=document) {
//			return Document.ObjectToBytes(document);
		}
		return null;
	}

	@Override
	public void close() {
	}
	
}
