[#ftl/]
[@b.head/]
[@b.toolbar title="报名管理 "/]
<div>
    <table class="indexpanel">
        <tr>
            <td class="index_view">
            [@b.form theme="search" name="examSignupsearchForm" action="!search" title="ui.searchForm" target="examSignupList"]
                <input type="hidden" name="info" value="${(info)!}"/>
                [@b.select name="fake.signupSeason.id" label="报名开关"]
                    <option value="">...</option>
                    [#list seasons! as season]
                        <option value="${season.id}">${season.name}</option>
                    [/#list]
                [/@]
                [@eams.semesterCalendar theme="search" label="学年学期" name="semester.id" empty=true value=semester/]
                <input type="hidden" name="examSignup.std.project.id" value="${projectContext.projectId}" />
                <input type="hidden" name="project.id" value="${projectContext.projectId}" />
                [@b.textfield name="examSignup.std.code" label="std.code"/]
                [@b.textfield name="examSignup.std.name" label="姓名"/]
                [@b.textfield name="examSignup.std.grade" label="年级"/]
                [@b.select name="examSignup.std.education.id" id="educationTypeId" label="entity.education" items=educations empty="..."/]
                [@b.select name="examSignup.std.department.id" label="entity.department" items=departments empty="..." /]
                [@b.select name="examSignup.subject.category.id" label="考试类型" items=examCategories empty="..."/]
                [@b.select name="examSignup.subject.id" label="报名科目" items=examSubjects empty="..." /]
                [@b.select name="examSignup.campus.id" label="entity.campus" items=campuses empty="..." /]
                [@b.textfield name="examSignup.std.adminclass.name" label="班级名称"/]
                [@b.startend label="报名开始,结束" name="signupAt_start,signupAt_end"  /]
                [@b.select name="examSignup.takeBus" label="乘坐校车" items={'1':'${b.text("common.yes")}','0':'${b.text("common.no")}'} empty="..." /]
            [/@]
            </td>
            <td class="index_content">
                [@b.div id="examSignupList" href="!search?semester.id="+(semester.id)!/]
            </td>
        </tr>
    </table>
</div>
[@b.foot/]