local internet = require("internet")
local component = require("component")

local interface = component.block_refinedstorage_interface

local CHUNK_SIZE = 15
local TRANSMIT_CHUNK_SIZE = 1000
local ADDRESS = ""

function escape_string(str)
    ret = ""

    for i = 1, #str do
        c = string.sub(str, i, i)

        if c == "\"" then
            ret = ret .. "\\\""
        else
            ret = ret .. c
        end
    end

    return ret
end

function transmitData(tbl)
    local data = "["
    local dataChunk = ""
    local currentSize = 0
    local tx_chunk = ""

    for k, v in pairs(tbl) do
        if type(v) == "table" then
            currentSize = currentSize + 1

            if (data ~= "[") or (dataChunk ~= "") then
                dataChunk = dataChunk..","
            end

            dataChunk = dataChunk .. "{\"name\":\"" .. v["name"] .. "\",\"label\":\"" .. escape_string(v["label"]) .. "\""
            dataChunk = dataChunk .. ",\"size\":" .. tostring(v["size"]) .. ",\"damage\":" .. tostring(v["damage"])
            dataChunk = dataChunk .. ",\"maxSize\":" .. tostring(v["maxSize"]) .. "}"

            if currentSize % CHUNK_SIZE == 0 then
                data = data .. dataChunk
                dataChunk = ""
            end
        end
    end

    if #dataChunk > 0 then
        data = data .. dataChunk
    end

    data = data .. "]"
    
    internet.request(ADDRESS, "new data", {["update"] = true}, "POST")

    for i = 1, #data do
        tx_chunk = tx_chunk .. string.sub(data, i, i)

        if i % TRANSMIT_CHUNK_SIZE == 0 then
            io.write(internet.request(ADDRESS, "chunk #" .. tostring(i / TRANSMIT_CHUNK_SIZE), {["content"] = tx_chunk}, "POST") ())

            tx_chunk = ""

            os.sleep(0.5)
        end
    end

    io.write(internet.request(ADDRESS, "remaining chunk", {["content"] = tx_chunk}, "POST")())

    internet.request(ADDRESS, "new data end", {["update_done"] = true}, "POST")
end

while true do
    io.write("Transmitting data")
    local success, err = pcall(transmitData(interface.getItems()))


    if (not success) then
        io.write("\nThere was an error. Waiting for OC to close current connections. (10s)\n")

        os.sleep(10)

        io.write("Retrying transmission.\n")
    else
        io.write("\nDone.\n")
    
        os.sleep(60)
    end
end