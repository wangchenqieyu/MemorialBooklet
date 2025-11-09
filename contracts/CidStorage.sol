pragma solidity ^0.8.0;

contract CidStorage {
    string private _cid;

    function storeCID(string calldata cid) external {
        _cid = cid;
    }

    function getMyCID() external view returns (string memory) {
        return _cid;
    }
}
