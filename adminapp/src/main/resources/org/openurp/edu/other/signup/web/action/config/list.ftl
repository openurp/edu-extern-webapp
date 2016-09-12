[#ftl]
[@b.head/]
[@b.form name="otherExamSignUpConfigListForm" action="!search" target="otherExamSignUpConfigList"]
    [@b.grid items=otherExamSignUpConfigs var="otherExamSignUpConfig"]
        [@b.gridbar title="校外考试开关维护"]
            bar.addItem("${b.text("action.new")}",action.add());
            bar.addItem("${b.text("action.edit")}",action.edit());
            bar.addItem("${b.text("action.delete")}",action.remove());    
            bar.addItem("科目维护","editSetting()");
        [/@]
        [@b.row]
            [@b.boxcol/]
            [@b.col property="category.name" title="考试类型"/]
            [@b.col property="code" title="开关代码"/]
            [@b.col property="name" title="开关名称"]
                [@b.a title="查看详细信息" href="config!info?otherExamSignUpConfig.id=${(otherExamSignUpConfig.id)!}"]${(otherExamSignUpConfig.name)!}[/@]
            [/@]
            [@b.col property="semester.id" title="common.semester"  width="15%"]${otherExamSignUpConfig.semester.schoolYear}-${otherExamSignUpConfig.semester.name}[/@]
            [@b.col property="beginAt" title="开始时间"]${otherExamSignUpConfig.beginAt?string("yyyy-MM-dd HH:mm")}[/@]
            [@b.col property="endAt" title="结束时间"]${otherExamSignUpConfig.endAt?string("yyyy-MM-dd HH:mm")}[/@]
            [@b.col property="opened" title="是否开放"]${otherExamSignUpConfig.opened?string("${b.text('common.yes')}","${b.text('common.no')}")}[/@]
        [/@]
    [/@] 
[/@]
<script>
    function editSetting(){
        var otherExamSignUpConfigIds=bg.input.getCheckBoxValues('otherExamSignUpConfig.id');
        if(otherExamSignUpConfigIds==''||otherExamSignUpConfigIds.indexOf(',')!=-1){
            alert("请仅选择一条记录!");
            return false;
        }
        bg.form.addInput(document.otherExamSignUpConfigListForm,"otherExamSignUpSetting.config.id",otherExamSignUpConfigIds);
        bg.form.submit(document.otherExamSignUpConfigListForm,"setting!search.action");
    }
</script>
[@b.foot/]