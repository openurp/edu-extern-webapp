[#ftl]
[@b.head/]
[@b.toolbar title="开关设置"]
[/@]
<table class="indexpanel">
<tr>
    <td class="index_view">
        [@b.form name="otherExamSignUpConfigsearchForm" action="!search" title="ui.searchForm" target="otherExamSignUpConfigList" theme="search"]
            <input type="hidden" name="otherExamSignUpConfig.project.id" value="${projectId}" />
            [@b.select name="otherExamSignUpConfig.category.id" label="考试类型" items=otherExamCategries empty="..." /]
            [@b.textfield name="otherExamSignUpConfig.name" label="开关名称"/]
            [@b.select name="otherExamSignUpConfig.opened" label="是否开放" items={"1":"是","0":"否"} empty="..." /]
        [/@]
    </td>
    <td class="index_content">
        [@b.div id="otherExamSignUpConfigList"/]
    </td>
</tr>
</table>
<script>
    jQuery(function(){
        bg.form.submit(document.otherExamSignUpConfigsearchForm);
    });
    
    function reset() { /*alert("111111");*/ }
</script>
[@b.foot/]