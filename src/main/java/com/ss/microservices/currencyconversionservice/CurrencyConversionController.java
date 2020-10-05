package com.ss.microservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("from",from);
        parameterMap.put("to",to);
        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, parameterMap);
        CurrencyConversionBean currencyConversionBean = responseEntity.getBody();
        currencyConversionBean.setTotalCalculatedAmount(quantity.multiply(currencyConversionBean.getConversionMultiple()));

        return currencyConversionBean;
    }

    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
        CurrencyConversionBean currencyConversionBean = currencyExchangeProxy.retrieveExchangeValue(from,to);
        currencyConversionBean.setTotalCalculatedAmount(quantity.multiply(currencyConversionBean.getConversionMultiple()));
        logger.info("{}",currencyConversionBean);
        return currencyConversionBean;
    }
}
