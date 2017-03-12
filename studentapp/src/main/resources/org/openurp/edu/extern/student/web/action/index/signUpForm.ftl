[#ftl]
[@b.head/]
    [#assign toolBarTitle]${((setting.config.notice!)=="")?string("二","三")}[/#assign]
    [@b.toolbar title="校外考试报名(第"+toolBarTitle+"步)"]
        bar.addBack();
         
        function signUp(){
            if(confirm("确定提交?")){
                bg.form.submit(document.actionForm);
            }
        }
    [/@]    
    
[@b.form name="actionForm" action="!save" method="post" target="main"]
    <table class="formTable" width="80%" align="center">
            <input type="hidden" name="setting.id" value="${setting.id}"/>
            <input type="hidden" name="isNeedDist" id="isNeedDist" value="${((setting.config.campuses)?size>0)?string}"/>
            <input type="hidden" name="campusIds" id="campusIds" value=""/>
            
            <tr class="darkColumn">
                <td colspan="4">个人信息确认</td>
            </tr>
            <tr>
                <td class="title">学号:</td>
                <td>${student.code?if_exists}</td>
                <td class="title">姓名:</td>
                <td>${student.person.formatedName?if_exists}</td>
            </tr>
            <tr>
                <td class="title" width="15%">年级</td>
                <td> ${student.grade?if_exists}</td>
                <td class="title" width="15%">出生日期</td>
                <td>[#if (student.person.birthday)??]${student.person.birthday?string("yyyy-MM-dd")}[/#if]</td>
            </tr>
            <tr>
                <td class="title" width="15%">所属院系</td>
                <td> ${student.department.name?if_exists}</td>
                <td class="title" width="15%">专业</td>
                <td> ${student.major.name?if_exists}</td>
             </tr>
            <tr>
                <td class="title">证件号码:</td>
                <td colspan='3'>${(student.person.code)?if_exists}</td>
            </tr>
            
            <tr class="darkColumn" style="border-top-width:1;border-color:#006CB2;">
                <td colspan="4">报名信息确认</td>
            </tr>
        <tr>
            <td class="title">报名科目:</td>
            <td>${setting.subject.name}</td>
            <td class="title">报名费:</td>
            <td>${setting.feeOfSignUp?default(0)} RMB</td>   
        </tr>
        <tr>
            <td class="title">考试校区:</td>
            <td colspan="3">
            [#if setting.config.allowCrossCampus]
                <select name="otherExamSignUp.campus.id" id="otherExamSignUp.campuses.id">
                    [#list (setting.config.campuses)?sort_by("name") as campus]
                           <option value="${campus.id}">${campus.name}</option>
                    [/#list]
                </select>
                <span style="display:none">是否乘坐班车:<select name="signUp.takeBus" id="signUp.takeBus" style="width:100px"><option value="0">否</option><option value="1">是</option></select></span>
            </td>
            [#else]
                <select name="otherExamSignUp.campus.id" id="otherExamSignUp.campuses.id">
                     <option value="${(student.state.campus.id)!}">${(student.state.campus.name)!}</option>
                </select>
            [/#if]
            </td>
        </tr>
        [#if setting.feeOfMaterial??]
        <tr>
            <td class="title">是否需要材料:</td>
            <td>
               <select name="needMaterial" style="width:100px">
                <option value="1">是</option>
                <option value="0" selected>否</option>
               </select>
            </td>
            <td class="title">材料费:</td>
            <td>${setting.feeOfMaterial?default(0)} RMB</td>
        </tr>
        [/#if]
        [#if setting.feeOfOutline??]
        <tr>
            <td class="title">是否需要考纲:</td>
            <td>
               <select name="needOutline" style="width:100px">
                <option value="1">是</option>
                <option value="0" selected>否</option>
               </select>
            </td>
            <td class="title">考纲费:</td>
            <td>${setting.feeOfOutline?default(0)} RMB</td>
        </tr>
        [/#if]
        <tr>
            <td colspan="4" align="center"><input type="button" onclick="signUp()" style="width:80px" value="报名"/></td>
        </tr>
    </table>
[/@]
[@b.foot/]