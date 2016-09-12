[#ftl]
[@b.head/]
[@b.toolbar title=labInfo]
    bar.addBack();
[/@]
[@b.form name="otherExamSignUpConfigForm" action="!save" title="开关维护" theme="list"]
    [@b.select  name="otherExamSignUpConfig.semester.id" required="true" id="semestersId"  style="width:200px" label="学期"]
        [#list semesters?sort_by("code")?reverse as semester]
        <option value="${semester.id}" [#if (otherExamSignUpConfig.semester.id)?? && semester.id==otherExamSignUpConfig.semester.id]selected[/#if] title="${semester.schoolYear}学年第${semester.name?replace('0','')}学期">${semester.schoolYear}学年第${semester.name?replace('0','')}学期</option>
        [/#list]
    [/@]
    [#if (otherExamSignUpConfig.id)?exists]
        [@b.field label="考试类型"]${(otherExamSignUpConfig.category.name)}[/@]
    [#else]
        [@b.select name="otherExamSignUpConfig.category.id" id="categoryId" items=otherExamCategories?sort_by("code") label="考试类型" value="${(otherExamSignUpConfig.category.id)!}" required="true" style="width:200px"/]
    [/#if]
    [@b.select  name="otherExamSignUpConfig.opened" required="true" id="openId"  style="width:200px" label="是否开放" ]
      <option value="1" [#if (otherExamSignUpConfig.opened)]selected[/#if]>${b.text("common.yes")}</option>
      <option value="0" [#if (!otherExamSignUpConfig.opened)]selected[/#if]>${b.text("common.no")}</option>
    [/@]
    [@b.textfield name="otherExamSignUpConfig.code" label="开关代码" value="${(otherExamSignUpConfig.code)!}" required="true" style="width:200px"/]
    [@b.textfield name="otherExamSignUpConfig.name" label="开关名称" value="${(otherExamSignUpConfig.name)!}" required="true" style="width:200px"/]
    [@b.startend label="报名开放时间段" readOnly="readOnly"  required="true"  name="otherExamSignUpConfig.beginAt,otherExamSignUpConfig.endAt" required="true" start=otherExamSignUpConfig.beginAt end=otherExamSignUpConfig.endAt format="yyyy-MM-dd HH:mm:ss" /]
    [@b.textarea name="otherExamSignUpConfig.notice" maxlength="1000" label="承诺书" value=(otherExamSignUpConfig.notice)! cols="60" rows="4" style="width:200px"/]
    <tr>
    [#if !((otherExamSignUpConfig.id)?exists)]
    [@b.field label="使用默认科目"]
        <select name="createDefaultSubject" style="width:200px">
            <option value="1" selected>${b.text("common.yes")}</option>
            <option value="0">${b.text("common.no")}</option>
        </select>
    [/@]
    [/#if]
    [@b.field label="科目冲突设置"]
    <table class="formTable" style="width: 500px">
        <tr>
            <td>科目一</td>
            <td></td>
            <td>所有科目</td>
            <td></td>
            <td>科目二</td>
        </tr>
        <tr>
            <td>
            <select name="subjectOne" id="subjectOne" size="5" style="width: 200px" multiple="multiple" theme="xml" onDblClick="moveSelectOption('subjectOne','subjectList')">
                [#list firstCategories as subject]
                <option value="${subject.id}" title="${(subject.name)!}">${subject.name}</option>
                [/#list]
            </select>
            </td>
            <td>
                <input type="button" name="toSubject1Button" value="&lt;&lt;" OnClick="moveSelectOption('subjectList','subjectOne')" />
                <br>
                <input type="button" name="backSubject1Button" value="&gt;&gt;" OnClick="moveSelectOption('subjectOne','subjectList')" />
            </td>
            <td>
            <select name="subjectList" id="subjectList" size="5" style="width: 200px" multiple="multiple" theme="xml">
                [#list subjects?if_exists as subject]
                 <option value="${subject.id}" title="${(subject.name)!}">${subject.name?if_exists}</option>
                [/#list] 
            </select>
            </td>
            <td>
                <input type="button" name="backSubject2Button" value="&lt;&lt;" OnClick="moveSelectOption('subjectTwo', 'subjectList')" />
                <br>
                <input type="button" name="toSubjectTwoButton" value="&gt;&gt;"    OnClick="moveSelectOption('subjectList', 'subjectTwo')" />
            </td>
            <td>
                <select name="subjectTwo" id="subjectTwo" size="5" style="width: 200px" multiple="multiple" theme="xml" onDblClick="moveSelectOption('subjectTwo','subjectList')">
                [#list secondCategories as subject]
                <option value="${subject.id}" title="${(subject.name)!}">${subject.name}</option>
                [/#list]
            </select></td>
        </tr>
    </table>
    [/@]
    [@b.radios label="允许跨校区报名" name="otherExamSignUpConfig.allowCrossCampus" items="1:是,0:否" value=otherExamSignUpConfig.allowCrossCampus required="true"/]
    [@b.field label="报名考试校区" required="true"]
        <table style="border-collapse: collapse;border:solid;border-width:1px;border-color:#006CB2;background-color:#b6d0ff;">
            <tr>
                <td>
                    <select name="campusList" id="campusList" multiple="multiple" theme="xml"  style="width:200Px;height:100px" onDblclick="JavaScript:bg.select.moveSelected(this.form['campusList'], this.form['selectCampus'])" >
                    [#list campuses as campus]
                    <option value="${campus.id}">${campus.name?if_exists}</option>
                    [/#list]
                    </select>
                </td>
                <td style="width:30px">
                    <input style="margin:auto" onclick="JavaScript:bg.select.moveSelected(this.form['campusList'], this.form['selectCampus'])" type="button" value="&gt;&gt;"/>
                    <input style="vertical-align: middle;" onclick="JavaScript:bg.select.moveSelected(this.form['selectCampus'], this.form['campusList'])" type="button" value="&lt;&lt;"/>
                </td>
                <td>
                    <select name="selectCampus" id="selectCampus" multiple="multiple"  style="width:200px;height:100px" onDblclick="JavaScript:bg.select.moveSelected(this.form['selectCampus'], this.form['campusList'])">
                    [#list (otherExamSignUpConfig.campuses)?if_exists as selectCampus]
                    <option value="${selectCampus.id}">${selectCampus.name}</option>
                    [/#list]
                    </select>
                </td>
            </tr>
        </table>
    [/@] 
     [@b.textarea name="otherExamSignUpConfig.remark" maxlength="100" label="${b.text('common.remark')}" value=(otherExamSignUpConfig.remark)! style="width:500px" cols="60" rows="4" /]
    [@b.formfoot]
        
        <input type="hidden" name="otherExamSignUpConfig.id" value="${(otherExamSignUpConfig.id)!}"/>
        [@b.submit value="action.submit" onsubmit="presubmit()" /]
        [@b.reset/]
    [/@]
[/@]
<script language="JavaScript" >
    [#--
    jQuery(document).ready(function(){
        jQuery("#categoryId").change(function(){
            jQuery.post("${base}/signup/config!getOtherExamSubjects.action",{categoryId:jQuery(this).val()},function(data){
                jQuery("#subjectList").empty();
                jQuery("#subjectList").append(data);
            },"text");
        })
    });
    --]
    
    [#if !(otherExamSignUpConfig.id)??]
        jQuery(function(){
            jQuery("#categoryId").data("categorySubjects",{});
            [#if categorySubjects??]
                [#list categorySubjects.entrySet() as entry]
                    if(!(jQuery("#categoryId").data("categorySubjects")["${entry.key}"])){
                        jQuery("#categoryId").data("categorySubjects")["${entry.key}"] = "";
                    }
                    [#list entry.value as oneCategorySubject]
                        jQuery("#categoryId").data("categorySubjects")["${entry.key}"] = "<option value=\"${oneCategorySubject.id}\" title=\"${(oneCategorySubject.name)!}\">${(oneCategorySubject.name)!}</option>";
                    [/#list]
                [/#list]
                jQuery("#categoryId").change();
            [/#if]
        });
    
    
        jQuery("#categoryId").change(function(){
            var data = jQuery("#categoryId").data("categorySubjects")[jQuery("#categoryId").val()];
            jQuery("#subjectList").empty().append(data);
            jQuery("#subjectOne").empty();
            jQuery("#subjectTwo").empty();
        });
    [/#if]
    var form = document.otherExamSignUpConfigForm;
    function categoryChange(){
        var category=document.getElementById("categoryId");
        otherExamSignUpService.getSubjects(category.options[category.selectedIndex].value,setSelectSubject);
    }
    function setSelectSubject(subjects){
        subjectList=document.getElementById("subjectOne");
        subjectList.options.length=0;
        subjectList=document.getElementById("subjectTwo");
        subjectList.options.length=0;
        subjectList=document.getElementById("subjectList");
        subjectList.options.length=0;
        for(var prop in subjects){
            subjectList.options.add(new Option(subjects[prop][1],subjects[prop][0]));
        }
    }
    function removeSelectedOption(select){
        var options = select.options;
        for (var i=options.length-1; i>=0; i--){   
            if (options[i].selected){
                options[i] = null;
            }
        }
    }
    function moveSelectOption(select1, select2){
        var node1=document.getElementById(select1);
        var node2=document.getElementById(select2);
        moveSelectedOption(node1, node2);
    }
    function moveSelectedOption(srcSelect, destSelect){
        for (var i=0; i<srcSelect.length; i++){
            if (srcSelect.options[i].selected){ 
                var op = srcSelect.options[i];
                if (!hasOption(destSelect, op)){
                    destSelect.options[destSelect.length]= new Option(op.text, op.value);
                }
            }
        }
        removeSelectedOption(srcSelect);
        clearSelectStatus(srcSelect);
    }
    function clearSelectStatus(select){
        //CLEAR
        for(var i=0; i<select.length; i++)
            select.options[i].selected = false;
    }
    function hasOption(select, op){
        for (var i=0; i<select.length; i++ ){
            if (select.options[i].value == op.value)
                return true;
        }
        return false;
    }
    
    function presubmit(){
        jQuery("#subjectOne option").each(function(){
            jQuery(this).prop("selected",true);
        })
        jQuery("#subjectTwo option").each(function(){
            jQuery(this).prop("selected",true);
        })
        jQuery("#selectCampus option").each(function(){
            jQuery(this).prop("selected",true);
        })
        
        if (!checkSelectRequire("selectCampus")) {
            jQuery("#selectCampus").after("<label class='error'>请选择考试报名校区</label>");
            return false;
        }
        return true;
    }
    
    function checkSelectRequire(selectId) { 
        var selectObj = document.getElementById(selectId);
        for (i =0; i<selectObj.options.length; i++) { if (selectObj.options[i].selected) return true; } return false;
    }
    
    function reload(){

    }
</script>
[@b.foot/]