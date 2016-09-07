[#ftl]
[@b.head/]
[@b.toolbar title="开关科目维护"]
    bar.addBack();
[/@]
[@b.grid items=otherExamSignUpSettings var="otherExamSignUpSetting" ]
    [@b.gridbar]
        bar.addItem("${b.text("action.add")}","add()");
        bar.addItem("${b.text("action.edit")}","edit()");
        bar.addItem("${b.text("action.delete")}","remove()");        
        bar.addItem("批量修改","batchEdit()");
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="subject.name" title="报名科目"/]
        [@b.col property="subject.code" title="科目代码"/]
        [@b.col property="feeOfSignUp" title="报名费"/]
        [@b.col property="feeOfMaterial" title="材料费"/]
        [@b.col property="feeOfOutline" title="考纲费"/]
        [@b.col property="maxStd" title="最大学生数"/]
        [@b.col property="grade" title="年级"]${otherExamSignUpSetting.gradePermited?string('允许','限制')}:${(otherExamSignUpSetting.grade)!('无')}[/@]
        [@b.col property="reExamAllowed" title="能够重考"]${(otherExamSignUpSetting.reExamAllowed?default(false))?string("${b.text('common.yes')}","${b.text('common.no')}")}[/@]
        [@b.col property="superSubject.name" title="须过科目"/]
        [@b.col property="beginAt" title="考试开始时间" width="15%"]${(otherExamSignUpSetting.beginAt?string("yy-MM-dd HH:mm"))?if_exists}[/@]
        [@b.col property="endAt" title="考试结束时间" width="15%"]${(otherExamSignUpSetting.endAt?string("yy-MM-dd HH:mm"))?if_exists}[/@]
    [/@]
[/@]

[@b.form name="otherExamSignUpSettingPublicForm" target="otherExamSignUpConfigList"]
    <input type="hidden" name="otherExamSignUpSetting.config.id" id="otherExamSignUpSetting.config.id" value="${(config.id)!}"/>
[/@]

<script>
    function batchEdit(){
        document.otherExamSignUpSettingPublicForm.action="otherExamSignUpSetting!batchEdit.action";
        var settingIds=bg.input.getCheckBoxValues("otherExamSignUpSetting.id");
        if(settingIds==""){
            alert("请至少选择一个进行操作!");
            return false;
        }
        bg.form.addInput(document.otherExamSignUpSettingPublicForm,"otherExamSignUpSettingIds",settingIds);
        bg.form.submit(document.otherExamSignUpSettingPublicForm);
        
    }
    
    function edit(){
        document.otherExamSignUpSettingPublicForm.action="otherExamSignUpSetting!edit.action";
        var settingId=bg.input.getCheckBoxValues("otherExamSignUpSetting.id");
        if(settingId==""||settingId.indexOf(',')!=-1){
            alert("请仅选择一个进行操作!");
            return;
        }
        bg.form.addInput(document.otherExamSignUpSettingPublicForm,"otherExamSignUpSetting.id",settingId);
        bg.form.submit(document.otherExamSignUpSettingPublicForm);
    }
    
    function add(){
        document.otherExamSignUpSettingPublicForm.action="otherExamSignUpSetting!edit.action";
        bg.form.submit(document.otherExamSignUpSettingPublicForm);
    }
    
    function remove(){
        document.otherExamSignUpSettingPublicForm.action="otherExamSignUpSetting!remove.action";
        var settingIds=bg.input.getCheckBoxValues("otherExamSignUpSetting.id");
        if(settingIds==""){
            alert("请至少选择一个进行操作!");
            return;
        }
        bg.form.addInput(document.otherExamSignUpSettingPublicForm,"otherExamSignUpSetting.id",settingIds);
        bg.form.submit(document.otherExamSignUpSettingPublicForm);
    }
    
</script>
[@b.foot/]