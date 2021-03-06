package com.example.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CurrencyExchangeServiceProxy proxy;

	@GetMapping(path = "/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConverterBean getConvertedValues(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		HashMap<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConverterBean> bean = new RestTemplate().getForEntity(
				"http://localhost:4000/currency-exchange/from/{from}/to/{to}", CurrencyConverterBean.class,
				uriVariables);
		CurrencyConverterBean response = bean.getBody();
		return new CurrencyConverterBean(response.getId(), to, from, response.getConversionMultiple(), quantity,
				quantity.multiply(response.getConversionMultiple()), response.getPort());
	}

	@GetMapping(path = "/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConverterBean getConvertedValuesFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		CurrencyConverterBean response = proxy.getConvertedValues(from, to);
		logger.info("{}", response);
		return new CurrencyConverterBean(response.getId(), to, from, response.getConversionMultiple(), quantity,
				quantity.multiply(response.getConversionMultiple()), response.getPort());

	}

}
