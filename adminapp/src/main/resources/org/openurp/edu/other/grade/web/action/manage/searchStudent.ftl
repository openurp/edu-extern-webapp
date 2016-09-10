[#ftl]
[#if student??]
{
student : {id : ${student.id}, name : "${(student.person.name?js_string)!}"}
}
[/#if]