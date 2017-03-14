[#ftl/]
[@b.head/]
[@b.toolbar title="学生成绩模版导入"]
    bar.addItem("模板下载","downloadTemplate()","${base}/static/images/action/download.gif");
[/@]
[@b.form name="awardImportForm" action="!importData" theme="list" enctype="multipart/form-data"]
    [@b.messages/]
    <label for="importFile" class="label">文件目录:</label><input type="file" name="importFile" value="" id="importFile"/>
    <br>
    <div style="padding-left: 110px;">
        [@b.submit value="system.button.submit" onsubmit="validateExtendName"/]
        <input type="reset" value="${b.text("system.button.reset")}" class="buttonStyle"/>
        
    </div>
    <div style="color:red;font-size:2">上传文件中的所有信息均要采用文本格式。对于日期和数字等信息也是一样。</div>
[/@]
[@b.form name="actionForm"/]
<script type="text/javascript">
    function downloadTemplate() {
        var actionForm = document.actionForm;
        actionForm.target = "_self";
        bg.form.submit(actionForm, "manage!downloadTemplate.action");
        action.target = "examGradeList";
    }
    function validateExtendName(form){
        var value = form.importFile.value;
        var index = value.indexOf(".xls");
        if((index < 1) || (index < (value.length - 4))){
            alert("${b.text("filed.file")}'.xls'");
            return false;
        }
        $("#semesterId").val($("input[name='assistantSemester']").val());
        return true;
    }
</script>
[@b.foot/]