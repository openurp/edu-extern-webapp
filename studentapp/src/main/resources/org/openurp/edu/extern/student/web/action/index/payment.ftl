[#ftl]
[@b.head/]
[#if paymentCheckMsg??]
        ${paymentCheckMsg}
[#else]
    <form id="paymentForm" name="paymentForm" action="${paymentUrl!}">
        [#list payParams?keys as key]
            <input type="hidden" name="${key}" value="${payParams.get(key)}"/>
        [/#list]
    </form>
    <script type="text/javascript">
        jQuery(function(){
            jQuery.post("${base}/${paymentActionURL}!publishPayingEvent.action?bill.id=${bill.id}",function(){
                document.paymentForm.submit();
            });
        });
    </script>
[/#if]
