[#ftl]
[@b.head/]
<style type="text/Css">
    .resultTb{
        border-collapse: collapse;
        border: solid;
        border-width: 1px;
        border-color: #e3e8bf;
        background:#feffed;
        width:60%;
          cellpadding: 0;
           cellspacing: 0;
           margin-top:2%;
     }

     .returnRetakeIndex a:link {
        color:#afddf2
        text-decoration: none;
    }
    .returnRetakeIndex a:visited {
        color:#7fbbd5;
        text-decoration: none;
    }

    .returnRetakeBillList a:link {
        color:#7fbbd5
        text-decoration: none;
    }
    .returnRetakeBillList a:visited {
        color:#7fbbd5;
        text-decoration: none;
    }

    .retakePaymentSubmit a:link {
        color:#514cc0
        text-decoration: none;
    }

    .retakePaymentSubmit a:visited {
        color:#514cc0;
        text-decoration: none;
    }

    .resultTb a:hover {
        color: red;
    }
</style>
<center>
    <table class="resultTb" height="120px">
        <tr><td colSpan="2"></td></tr>
        <tr>
            <td rowSpan="3" width="80px" style="vertical-align:top;text-align:center">
                <img style="margin-top:8px;" src="${base}/static/images/success.jpg"/>
            </td>
            <td style="font-size:14pt;" class="retakePaymentSubmit">
                如果没有自动弹出支付页面,请<a id="pop" href="#" rel="external" onClick="document.paymentForm.submit();" style="font-weight:bold;color:">&nbsp;点击这里&nbsp;</a>进行支付
            </td>
        </tr>
        <tr>
            <td style="font-size:11pt;" class="returnRetakeIndex">返回[@b.a href="!index"]&nbsp;重修缴费&nbsp;[/@]</td>
        </tr>
        <tr>
            <td style="font-size:11pt;" class="returnRetakeBillList">返回[@b.a href="!search" ]&nbsp;我的订单&nbsp;[/@]</td>
        </tr>
    </table>
    <form id="paymentForm" name="paymentForm" action="${paymentUrl!}" target="_blank">
        [#list payParams?keys as key]
            <input type="hidden" name="${key}" value="${payParams.get(key)}"/>
        [/#list]
    </form>
</center>
<script>
    jQuery(function(){
        document.paymentForm.submit();
    });
</script>
[@b.foot/]
