# ETHME
the App which makes interacting with ETHEREUM easy

As part of my Bachelor’s degree of computer science
at the FH Technikum Wien – University of Applied Sciences
ETHME is a open source study project with following Vision:
(You can also find the Whitepaper & other documents under the directory ./pdf)

Vision
ETHME in it’s mvp Version shall be a very basic Android App, 
which offers a basic Ether Wallet and the possibility to create 
customized ERC-20 Tokens on the ethereum blockchain within a few clicks. 
It uses the build in Wallet to deploy those ERC-20 Token to the ethereum virtual machine (EVM). 
The deployed ERC-20 contract shall also be viewable and manageable in a very basic way in the App. 
Which means seeing the contract address, the token metrics and also mint, transfer and burn them, 
if that’s doable in the scope of this project.

Further potential?
The App could support enhanced blockchain operations and more complex 
smart contract deployments in further releases. With regard of still 
keeping those operations as simple and safe for the users as possible.

Technologies
This will be an Android App, written in Java and using web3j Java/Android library 
to interact with the EVM over an ethereum node. For faster and 
more secure smart contract development, as far as needed for the mvp Version, 
an open framework as OpenZeppelin or a service as ZeppelinOS is likely to be used.

Project Scope & Organisation 
The project scope are 5 ECTS (European Credit Transfer System), 
which represent a workload of about 125h.
For this project 25 hours are planned for theory and learning more about ethereum, 
it’s EVM and how smart contracts and Dapps work. 
For that purpose, especially “Mastering Ethereum – Building Smart Contracts and Dapps” 
from Andreas M. Antonopoulos & Dr. Gavin Wood is used as learning material. 
(https://github.com/ethereumbook/ethereumbook)

The other 100 hours are reserved for implementing ETHME. After a successful implementation, 
ETHME should be an Android application in form of an .apk file which has really 
basic Wallet functionalities and should be able to deploy customizable ERC-20 Tokens on the real EVM with a simple UI.
The project end date is the October 14th 2019. 
Until then there’ll be a project status update every Friday as short PDF sent via email to the Tutor of this project. 
Unless otherwise agreed with the Tutor, the project hast above defined requirements to be done, to be seen as successful.

For time & task recording, a simple Google Sheet will be used.

As study project besides a working product of course, the focus also lies in learning the used technologies, as Android App development and web3j.