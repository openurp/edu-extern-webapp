[#ftl]
[@b.head/]
[@b.toolbar title="开关设置"]
[/@]
<table class="indexpanel">
<tr>
    <td class="index_view">
        [@b.form name="examSignupConfigsearchForm" action="!search" title="ui.searchForm" target="examSignupConfigList" theme="search"]
            <input type="hidden" name="examSignupConfig.project.id" value="${projectContext.projectId}" />
            [@b.select name="examSignupConfig.category.id" label="考试类型" items=examCategries empty="..." /]
            [@b.textfield name="examSignupConfig.name" label="开关名称"/]
            [@b.select name="examSignupConfig.opened" label="是否开放" items={"1":"是","0":"否"} empty="..." /]
        [/@]
    </td>
    <td class="index_content">
        [@b.div id="examSignupConfigList"/]
    </td>
</tr>
</table>
<script>
    jQuery(function(){
        bg.form.submit(document.examSignupConfigsearchForm);
    });

    function reset() { /*alert("111111");*/ }
</script>
[@b.foot/]
