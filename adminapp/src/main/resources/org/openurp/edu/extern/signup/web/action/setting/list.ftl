[#ftl]
[@b.head/]
[@b.toolbar title="开关科目维护"]
    bar.addBack();
[/@]
[@b.grid items=examSignupSettings var="examSignupSetting" ]
    [@b.gridbar]
        bar.addItem("${b.text("action.add")}","add()");
        bar.addItem("${b.text("action.edit")}","edit()");
        bar.addItem("${b.text("action.delete")}","remove()");
        bar.addItem("批量修改","batchEdit()");
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="subject.name" title="报名科目" width="19%"/]
        [@b.col property="feeOfSignup" title="报名费" width="6%"/]
        [@b.col property="feeOfMaterial" title="材料费" width="6%"/]
        [@b.col property="feeOfOutline" title="考纲费" width="6%"/]
        [@b.col property="maxStd" title="最大人数" width="7%"/]
        [@b.col title="条件" width="15%"][#list examSignupSetting.conditions as con] ${con.inclusive?string('允许','限制')}:${(con.grades)!('无')}  ${(con.level.name)!}[#if con_has_next]<br>[/#if][/#list][/@]
        [@b.col property="reExamAllowed" title="能够重考" width="7%"]${(examSignupSetting.reExamAllowed?default(false))?string("${b.text('common.yes')}","${b.text('common.no')}")}[/@]
        [@b.col property="superSubject.name" title="须过科目" width="10%"/]
        [@b.col property="examOn" title="考试日期" width="10%"/]
        [@b.col property="examBeginAt" title="考试时间" width="10%"][#if examSignupSetting.examBeginAt?string!='00:00']${examSignupSetting.examBeginAt}～${examSignupSetting.examEndAt}[#else]&nbsp;[/#if][/@]
    [/@]
[/@]

[@b.form name="examSignupSettingPublicForm" target="examSignupConfigList"]
    <input type="hidden" name="examSignupSetting.config.id" id="examSignupSetting.config.id" value="${(config.id)!}"/>
[/@]

<script>
    function batchEdit(){
        document.examSignupSettingPublicForm.action="setting!batchEdit.action";
        var settingIds=bg.input.getCheckBoxValues("examSignupSetting.id");
        if(settingIds==""){
            alert("请至少选择一个进行操作!");
            return false;
        }
        bg.form.addInput(document.examSignupSettingPublicForm,"examSignupSettingIds",settingIds);
        bg.form.submit(document.examSignupSettingPublicForm);

    }

    function edit(){
        document.examSignupSettingPublicForm.action="setting!edit.action";
        var settingId=bg.input.getCheckBoxValues("examSignupSetting.id");
        if(settingId==""||settingId.indexOf(',')!=-1){
            alert("请仅选择一个进行操作!");
            return;
        }
        bg.form.addInput(document.examSignupSettingPublicForm,"examSignupSetting.id",settingId);
        bg.form.submit(document.examSignupSettingPublicForm);
    }

    function add(){
        document.examSignupSettingPublicForm.action="setting!edit.action";
        bg.form.submit(document.examSignupSettingPublicForm);
    }

    function remove(){
        document.examSignupSettingPublicForm.action="setting!remove.action";
        var settingIds=bg.input.getCheckBoxValues("examSignupSetting.id");
        if(settingIds==""){
            alert("请至少选择一个进行操作!");
            return;
        }
        bg.form.addInput(document.examSignupSettingPublicForm,"examSignupSetting.id",settingIds);
        bg.form.submit(document.examSignupSettingPublicForm);
    }

</script>
[@b.foot/]
