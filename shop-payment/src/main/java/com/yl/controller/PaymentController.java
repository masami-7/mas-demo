package com.yl.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yl.annotations.LoginRequired;
import com.yl.bean.OmsOrder;
import com.yl.bean.PaymentInfo;
import com.yl.service.OrderService;
import com.yl.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

@Controller
public class PaymentController {

    @Autowired
    AlipayClient alipayClient;

    @Reference
    PaymentService paymentService;

    @Reference
    OrderService orderService;

    @RequestMapping("vx/submit")
    @LoginRequired(loginSuccess = true)
    public String vx(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        return null;
    }

    @RequestMapping("alipay/submit")
    @LoginRequired(loginSuccess = true)
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.page.pay
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        // 回调函数
        alipayRequest.setReturnUrl("http://localhost:9000/alipay/callback/return");
        alipayRequest.setNotifyUrl("http://localhost:9000/alipay/callback/notify");

        AlipayTradePayModel model = new AlipayTradePayModel();

        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setOutTradeNo(outTradeNo);
        model.setSubject("Iphone6 16G");
        model.setTotalAmount(String.valueOf(totalAmount));

        alipayRequest.setBizModel(model);

        String form = "";

        // 获得一个支付宝请求的客户端(它并不是一个链接，而是一个封装好的http的表单请求)
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();  //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 生成并且保存用户的支付信息
        OmsOrder omsOrder = orderService.getOrderByOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject("谷粒商城商品一件");
        paymentInfo.setTotalAmount(totalAmount);
        paymentService.savePaymentInfo(paymentInfo);

        // 向消息中间件发送一个检查支付状态(支付服务消费)的延迟消息队列
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNo, 5);

        return form;
    }

    @RequestMapping("alipay/callback/return")
    @LoginRequired(loginSuccess = true)
    public String aliPayCallBackReturn(HttpServletRequest request, ModelMap modelMap) {

        // 回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();


        // 通过支付宝的paramsMap进行签名验证，2.0版本的接口将paramsMap参数去掉了，导致同步请求没法验签
        if (StringUtils.isNotBlank(sign)) {
            // 验签成功
            // 更新用户的支付状态

            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(out_trade_no);
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);// 支付宝的交易凭证号
            paymentInfo.setCallbackContent(call_back_content);//回调请求字符串
            paymentInfo.setCallbackTime(new Date());

            paymentService.updatePayment(paymentInfo);

        }

        return "finish";
    }

//    @RequestMapping("alipay/callback/notify")
//    @LoginRequired(loginSuccess = true)
//    public String aliPayCallBackNotify(HttpServletRequest request) {
//        // 订单号
//        String orderCode = request.getParameter("out_trade_no");
//        // 订单状态
//        String orderStatus = request.getParameter("trade_status");
//
//        boolean result = false;
//        try {
//            result = AlipaySignature.rsaCheckV2(parameterToMap(request, true), SOPConstants.ALI_PAY_PUBLIC_KEY, ALI_PAY_CHARSET, ALI_PAY_SIGN_TYPE);
//        } catch (AlipayApiException e) {
//
//        }
//
//        // 验签成功
//        if (result) {
//            // 订单已支付
//            if (orderStatus.equals(TRADE_STATUS_SUCCESS) || orderStatus.equals(TRADE_STATUS_FINISHED)) {
//                // 更新订单状态
//                updateOrderInfo(orderCode, OrderPay.aliPay.getCode());
//
//                return "success";
//            } else {
//                logger.error("支付宝订单" + orderCode + "未支付，订单状态为：" + orderStatus + "！");
//                return "fail";
//            }
//        } else {
//            logger.error("支付宝订单" + orderCode + "异步回执接收失败，订单状态为：" + orderStatus + "！");
//            return "fail";
//        }
//    }

    @RequestMapping("index")
    @LoginRequired(loginSuccess = true)
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        modelMap.put("nickName", nickname);
        modelMap.put("outTradeNo", outTradeNo);
        modelMap.put("totalAmount", totalAmount);

        return "index";
    }
}
