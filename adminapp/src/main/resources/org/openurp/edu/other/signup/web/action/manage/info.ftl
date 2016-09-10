[#ftl/]
[@b.head/]
[@b.toolbar title="资格考试报名信息"]
    bar.addBack("${b.text("action.back")}");
[/@]
[#assign labInfo]${b.text("ui.building.info")}[/#assign]
<table class="infoTable" width="100%">
    <tr>
        <td rowspan="4" class="title">
        <img src="avatar/user!show.action?user.name=${otherExamSignUp.std.code}"
            width="80px" height="110px" />
        <td rowspan="4" class="content">${otherExamSignUp.std.code}<br>
        ${otherExamSignUp.std.name}</td>
        <td class="title" width="15%">学期:</td>
        <td class="content" width="35%">${otherExamSignUp.semester.schoolYear}${otherExamSignUp.semester.name}</td>
    </tr>
    <tr>
        <td class="title" width="15%">报名费:</td>
        <td class="content" width="35%">${otherExamSignUp.feeOfSignUp!}</td>
    </tr>
    <tr>
        <td class="title" width="15%">考纲费:</td>
        <td class="content" width="35%">${otherExamSignUp.feeOfOutline!}</td>
    </tr>
    <tr>
        <td class="title" width="15%">材料费:</td>
        <td class="content" width="35%">${otherExamSignUp.feeOfMaterial!}</td>
    </tr>
    <tr>
        <td class="title">院系:</td>
        <td class="content">${(otherExamSignUp.std.department.name)!}</td>
        <td class="title">考试科目:</td>
        <td class="content">${(otherExamSignUp.subject.category.name)!}[${(otherExamSignUp.subject.category.code)!}]</td>
    </tr>
    <tr>
        <td class="title">专业:</td>
        <td class="content">${(otherExamSignUp.std.major.name)!}</td>
        <td class="title">考试校区:</td>
        <td class="content">${(otherExamSignUp.campus.name)!}</td>
    </tr>
    <tr>
        <td class="title">报名时间:</td>
        <td class="content">${(otherExamSignUp.signUpAt?string("yyyy-MM-dd HH:mm:ss"))!}</td>
        <td class="title">准考证号:</td>
        <td class="content">${(otherExamSignUp.examNo)!}</td>
    </tr>
</table>
[@b.foot/]