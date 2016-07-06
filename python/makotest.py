from mako.template import Template
print(Template(r"hello $${} ${data}!").render(data = "world" ))
