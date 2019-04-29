import setuptools

with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="acumosauthenticator",
    version="0.0.1",
    author="Vaibhav Shirsat",
    author_email="vs00485966@techmahindra.com",
    description="Acumos Authenticator",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://gerrit.acumos.org/r/#/admin/projects/design-studio",
    packages=['acumosauthenticator'],
)
